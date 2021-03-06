package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.enum.ProcessPriority
import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage
import cn.itxia.chatbot.message.response.ImageResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.service.MemberAuthenticationService
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import cn.itxia.chatbot.service.message.AbstractMessageProcessService
import cn.itxia.chatbot.util.CommandWords
import cn.itxia.chatbot.util.StorageUtil
import cn.itxia.chatbot.util.StorageWrapper
import cn.itxia.chatbot.util.getLogger
import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

private val keywordList = StorageWrapper("KeywordItem.json", object : TypeReference<MutableList<KeywordItem>>() {})

private val learnResponse = listOf("知道啦~", "学会了😊", "懂了!", "好的~", "分かります~")

private fun getRandomLearnResponse(): String {
    return learnResponse[(Math.random() * learnResponse.size).toInt()]
}

@Service
private class Learn : AbstractCommandProcessService() {

    override val priority = ProcessPriority.KEYWORD_LEARN

    private val client = OkHttpClient()

    private val logger = getLogger()

    @Autowired
    private lateinit var memberAuthenticationService: MemberAuthenticationService

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && CommandWords.KEYWORD_LEARN.contains(command.commandName)
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        if (message !is QQGroupIncomingMessage) {
            return ProcessResult.next()
        }
        val qqID = message.event.sender.id.toString()

        if (!memberAuthenticationService.validateMemberQQ(qqID)) {
            return ProcessResult.reply("请先在后台系统验证QQ号.", true)
        }

        val split = command.argument.split(" ")

        when (split.size) {
            1 -> {
                //学习图片
                val images = message.event.message.filterIsInstance(Image::class.java)
                if (images.size != 1) {
                    return ProcessResult.reply(
                        textResponse = "格式:{关键词} {回复} 或 {关键词}{图片}",
                        quoteReply = true
                    )
                }

                //下载图片
                val image = images[0]
                GlobalScope.launch {
                    val imageUrl = image.queryUrl()
                    logger.info("图片URL:$imageUrl.")

                    val request = Request.Builder()
                        .url(imageUrl)
                        .build()
                    client.newCall(request).execute().apply {
                        if (isSuccessful) {
                            val bytes = body?.bytes()
                            if (bytes != null) {
                                val fileName = image.imageId.replace(Regex("^\\w"), "")
                                logger.info("写入文件:$fileName.")

                                val imageFile = StorageUtil.implementImageFile(fileName)
                                imageFile.writeBytes(bytes)
                                keywordList.add(
                                    KeywordItem(
                                        keyword = split[0],
                                        addBy = message.event.sender.id.toString(),
                                        imageFileName = fileName
                                    )
                                )
                            } else {
                                logger.error("转换bytes失败.")
                            }
                        }
                    }
                }

                return ProcessResult.reply(getRandomLearnResponse(), true)
            }
            2 -> {
                keywordList.add(
                    KeywordItem(
                        keyword = split[0],
                        addBy = qqID,
                        responseText = split[1]
                    )
                )

                return ProcessResult.reply(getRandomLearnResponse(), true)
            }
            else -> {
                return ProcessResult.reply(
                    textResponse = "格式:{关键词} {回复}",
                    quoteReply = true
                )
            }
        }
    }
}

@Service
private class Forget : AbstractCommandProcessService() {

    override val priority = ProcessPriority.KEYWORD_FORGET

    @Autowired
    private lateinit var memberAuthenticationService: MemberAuthenticationService

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && CommandWords.KEYWORD_FORGET.contains(command.commandName)
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        if (message !is QQGroupIncomingMessage) {
            return ProcessResult.next()
        }
        val qqID = message.event.sender.id.toString()
        if (!memberAuthenticationService.validateMemberQQ(qqID)) {
            return ProcessResult.reply("请先在后台系统验证QQ号.", true)
        }

        val argument = command.argument

        val isRemove = keywordList.remove {
            it.keyword == argument
        }

        val response = if (isRemove) {
            "已移除关键字${argument}."
        } else {
            "未找到关键词${argument}对应的回复."
        }

        return ProcessResult.reply(response, true)
    }
}

@Service
private class ReplyKeyword : AbstractMessageProcessService() {

    override val priority = ProcessPriority.KEYWORD_REPLY

    private val excludeGroups = listOf<Long>(887021867, 302228512, 926053788)

    override fun process(message: IncomingMessage): ProcessResult {
        if (message !is QQGroupIncomingMessage) {
            return ProcessResult.next()
        }

        if (excludeGroups.contains(message.event.group.id)) {
            return ProcessResult.next()
        }

        val matchItems = keywordList.getAll {
            message.content.contains(it.keyword)
        }

        //随机选择一个
        val responseMessage = matchItems.randomOrNull().let {
            when (true) {
                it == null -> null
                it.responseText != null -> TextResponseMessage(it.responseText)
                it.imageFileName != null -> ImageResponseMessage(StorageUtil.implementImageFile(it.imageFileName))
                else -> null
            }
        } ?: return ProcessResult.next()

        return ProcessResult.replyAndContinue(responseMessage)
    }

    private val map = mutableMapOf<String, Date>()
    private val interval = 30 * 1000

    /**
     * TODO
     * 一段时间内只触发一次.
     * */
    private fun responseThrottle(keyword: String) {
        val lastTrigger = map[keyword]
        if (lastTrigger == null) {
            map[keyword] = Date()
        } else {
            //compare time
            val shouldTrig = Date().time - lastTrigger.time > interval
        }
    }
}


private data class KeywordItem(
    val keyword: String,
    val addBy: String,
    val responseText: String? = null,
    val imageFileName: String? = null,
)
