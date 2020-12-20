package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage
import cn.itxia.chatbot.message.response.ImageResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.newBot
import net.mamoe.mirai.utils.BotConfiguration
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
    private var qqID: Long = 0

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
        val bot = BotFactory.newBot(qqID, qqPassword, fun BotConfiguration.() {
            fileBasedDeviceInfo("device.json")
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        })

        //登录
        bot.login()

        if (bot.isOnline) {
            print("QQ机器人已启动")
        }

        val listenGroupList = groupsToListen.split(",")

        //监听群消息
        bot.subscribeAlways<GroupMessageEvent> { event ->

            val qqGroupID = event.group.id.toString()

            //在@机器人
            val isAtMe = event.message.any {
                it is At && it.target == qqID
            }

            //读取纯文本，忽略mirai码
            val plainTextContent = event.message.filterIsInstance<PlainText>().joinToString { it.contentToString() }

            /**
             * 仅当是监听的QQ群时，才处理消息.
             * */
            if (listenGroupList.contains(qqGroupID)) {
                val responseList = replyService.replyMessage(
                    QQGroupIncomingMessage(
                        content = plainTextContent,
                        event = event,
                        isAtMe = isAtMe
                    )
                )
                responseList.forEach {
                    it.apply {
                        if (it is TextResponseMessage) {
                            if (shouldQuoteReply) {
                                quoteReply(it.content)
                            } else {
                                reply(it.content)
                            }
                        } else if (it is ImageResponseMessage) {
                            sendImage(it.image)
                        }
                    }
                }

            }


        }

        //挂起bot协程
        bot.join()
    }

}
