package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage
import cn.itxia.chatbot.message.response.ImageResponseMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.util.getLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.newBot
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.sendImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * @author zhenxi
 * 调用Mirai QQ机器人
 * */
@Service
class MiraiQQRobotService {

    @Autowired
    private lateinit var replyService: ReplyService

    @Value("\${itxia.bot.mirai.enable}")
    private var isEnable: Boolean = false

    @Value("\${itxia.bot.mirai.qqID}")
    private var qqID: Long = 0

    @Value("\${itxia.bot.mirai.qqPassword}")
    private lateinit var qqPassword: String

    @Value("\${itxia.bot.mirai.groupsToListen}")
    private lateinit var groupsToListen: String

    private val logger = getLogger()

    private var bot: Bot? = null

    init {
        GlobalScope.launch {
            startMiraiBot()
        }
    }

    /**
     * 启动/重启QQ机器人.
     * */
    suspend fun startMiraiBot() {
        if (!isEnable) {
            logger.info("QQ机器人未启用.")
            return
        }
        if (bot?.isOnline == true) {
            logger.info("重启QQ机器人...")
            bot?.close()
            bot = null
        }

        logger.info("正在启动QQ机器人...")

        BotFactory.newBot(qqID, qqPassword, fun BotConfiguration.() {
            fileBasedDeviceInfo("device.json")
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        }).also {
            it.login()
            if (it.isOnline) {
                logger.info("QQ机器人已上线.")
            }

            bot = it

            //监听群消息
            subscribeGroupMessage()

        }.join()
    }


    /**
     * 发送到QQ群.
     * */
    fun sendToGroup(groupID: Long, vararg messages: ResponseMessage) {
        val group = bot?.getGroup(groupID) ?: return logger.error("找不到QQ群$groupID.")
        messages.forEach {
            GlobalScope.launch {
                when (it) {
                    is TextResponseMessage -> {
                        group.sendMessage(it.content)
                    }
                    is ImageResponseMessage -> {
                        group.sendImage(it.image)
                    }
                    else -> {
                        logger.warn("未支持的消息类型.")
                    }
                }
            }
        }
    }


    /**
     * 监听群消息.
     * */
    private fun subscribeGroupMessage() {
        val listenGroupList = groupsToListen.split(",")
        //监听群消息
        bot?.subscribeAlways<GroupMessageEvent> { event ->
            val qqGroupID = event.group.id.toString()

            /**
             * 仅当是监听的QQ群时，才处理消息.
             * */
            if (listenGroupList.contains(qqGroupID)) {

                //在@机器人
                val isAtMe = event.message.any {
                    it is At && it.target == qqID
                }

                //读取纯文本，忽略mirai码
                val plainTextContent =
                    event.message.filterIsInstance<PlainText>().joinToString { it.contentToString() }


                val incomingMessage = QQGroupIncomingMessage(
                    content = plainTextContent,
                    event = event,
                    isAtMe = isAtMe
                )

                val replyCallback = fun(responseMessage: ResponseMessage) {
                    GlobalScope.launch {
                        responseMessage.let {
                            when (it) {
                                is TextResponseMessage -> {
                                    if (it.shouldQuoteReply) {
                                        quoteReply(it.content)
                                    } else {
                                        reply(it.content)
                                    }
                                }
                                is ImageResponseMessage -> {
                                    sendImage(it.image)
                                }
                                else -> {
                                    logger.warn("未支持的返回消息类型.")
                                }
                            }
                        }
                    }
                }

                //调用处理service
                replyService.replyMessage(incomingMessage, replyCallback)
            }
        }

    }

}
