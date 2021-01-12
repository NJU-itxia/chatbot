package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage
import cn.itxia.chatbot.message.response.ImageResponseMessage
import cn.itxia.chatbot.message.response.QQResponseMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.util.getLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.BotConfiguration
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
        sendToGroups(listOf(groupID), *messages)
    }

    /**
     * 发送到QQ群.
     * */
    fun sendToGroups(groupIDList: List<Long>, vararg messages: ResponseMessage) {
        groupIDList.mapNotNull { bot!!.getGroup(it) }.forEach { group ->
            messages.forEach { message ->
                GlobalScope.launch {
                    when (message) {
                        is TextResponseMessage -> {
                            group.sendMessage(message.toTextMessage())
                        }
                        is QQResponseMessage -> {
                            message.toMessageChain().sendTo(group)
                        }
                        is ImageResponseMessage -> {
                            group.sendImage(message.image)
                        }
                        else -> {
                            logger.warn("未支持的消息类型.")
                        }
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
        bot!!.eventChannel.subscribeAlways<GroupMessageEvent> { event ->
            val qqGroupID = event.group.id.toString()

            //仅当是监听的QQ群时，才处理消息.
            if (listenGroupList.contains(qqGroupID)) {

                val incomingMessage = QQGroupIncomingMessage(
                    event = event,
                )

                val replyCallback = fun(responseMessage: ResponseMessage) {

                    GlobalScope.launch {
                        responseMessage.let {
                            when (it) {
                                is TextResponseMessage -> {
                                    if (it.shouldQuoteReply) {
                                        subject.sendMessage(message.quote() + it.content)
                                    } else {
                                        subject.sendMessage(it.content)
                                    }
                                }
                                is QQResponseMessage -> {
                                    it.toMessageChain().sendTo(group)
                                }
                                is ImageResponseMessage -> {
                                    subject.sendImage(it.image)
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
