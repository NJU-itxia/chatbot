package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.QQFriendIncomingMessage
import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage
import cn.itxia.chatbot.message.incoming.QQTempIncomingMessage
import cn.itxia.chatbot.message.response.ImageResponseMessage
import cn.itxia.chatbot.message.response.QQResponseMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.util.getLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
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

    private val job = Job()

    private val scope = CoroutineScope(job)

    private var bot: Bot? = null

    init {
        scope.launch {
            startMiraiBot()
        }
    }

    /**
     * 启动/重启QQ机器人.
     * */
    suspend fun startMiraiBot() {
        //make sure lateinit vars are all init
        while (true) {
            if (
                this.qqID != 0L &&
                this::qqPassword.isInitialized &&
                this::groupsToListen.isInitialized
            ) {
                break
            }
            logger.info("等待lateinit vars初始化...")
            delay(1000)
        }

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
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD
        }).also {
            it.login()
            if (it.isOnline) {
                logger.info("QQ机器人已上线.")
            }

            bot = it

            //监听QQ消息
            subscribeGroupMessage()
            subscribeFriendMessage()
            subscribeTempMessage()

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
                scope.launch {
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
                    responseMessage.let {
                        if (it is TextResponseMessage && it.shouldQuoteReply) {
                            scope.launch {
                                subject.sendMessage(message.quote() + it.content)
                            }
                        } else {
                            subject.reply(it)
                        }
                    }
                }

                //调用处理service
                replyService.replyMessage(incomingMessage, replyCallback)
            }
        }

    }

    /**
     * 监听私聊消息.
     * */
    private fun subscribeTempMessage() {
        bot!!.eventChannel.subscribeAlways<GroupTempMessageEvent> { event ->
            val message = QQTempIncomingMessage(event)
            replyService.replyMessage(message).forEach {
                subject.reply(it)
            }
        }
    }

    /**
     * 监听好友消息.
     * */
    private fun subscribeFriendMessage() {
        bot!!.eventChannel.subscribeAlways<FriendMessageEvent> { event ->
            val message = QQFriendIncomingMessage(event)
            replyService.replyMessage(message).forEach {
                subject.reply(it)
            }
        }
    }

    /**
     * 扩展Mirai的回复函数.
     *
     * 相当于包装了subject.sendMessage().
     * */
    fun Contact.reply(message: ResponseMessage) {
        val contact = this
        scope.launch {
            message.let {
                when (it) {
                    is TextResponseMessage -> {
                        sendMessage(it.content)
                    }
                    is QQResponseMessage -> {
                        it.toMessageChain().sendTo(contact)
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

}
