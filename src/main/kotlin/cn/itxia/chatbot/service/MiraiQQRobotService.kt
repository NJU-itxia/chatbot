package cn.itxia.chatbot.service

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.util.IncomingMessage
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.join
import net.mamoe.mirai.message.GroupMessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * @author zhenxi
 *
 * 调用Mirai QQ机器人
 * */
@Service
class MiraiQQRobotService {

    @Autowired
    private lateinit var replyService: ReplyService

    @Value("\${itxia.bot.mirai.enable}")
    private var isEnable: Boolean = false

    @Value("\${itxia.bot.mirai.qqID}")
    private var qqID: Long = 0;

    @Value("\${itxia.bot.mirai.qqPassword}")
    private lateinit var qqPassword: String

    @Value("\${itxia.bot.mirai.groupsToListen}")
    private lateinit var groupsToListen: String

    private var isRunning = false


    suspend fun startMiraiBot() {
        if (isEnable && !isRunning) {
            start()
        }
    }

    private suspend fun start() {
        val bot = Bot(qqID, qqPassword)

        //登录
        bot.login()

        if (bot.isOnline) {
            print("QQ机器人已启动")
        }

        val listenGroupList = groupsToListen.split(",")

        //监听群消息
        bot.subscribeAlways<GroupMessageEvent> { event ->

            val qqGroupID = event.group.id.toString()

            //最近的一条消息
            val lastMessage = event.message[event.message.size - 1]

            /**
             * 仅当是监听的QQ群时，才处理消息.
             * */
            if (listenGroupList.contains(qqGroupID)) {
                val (shouldResponse, content, shouldQuoteReply) = replyService.replyMessage(
                    IncomingMessage(
                        senderID = qqGroupID,
                        messageFrom = MessageFrom.QQ_GROUP_CHAT,
                        content = lastMessage.contentToString(),
                        qqGroupMessageEvent = event
                    )
                )
                if (shouldResponse) {
                    if (shouldQuoteReply) {
                        quoteReply(content)
                    } else {
                        reply(content)
                    }
                }
            }

        }

        //挂起bot协程
        bot.join()
    }

}
