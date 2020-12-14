package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import org.springframework.stereotype.Service

@Service
class RepeaterService : MessageProcessService {

    override val order: Int
        get() = 64

    private var isEnable = false

    private val commandKeyWords = listOf("复读", "复读机")

    override fun process(message: IncomingMessage): ProcessResult {

        val split = message.content.split(" ")
        if (split.size == 2) {
            val (command, arg) = split
            if (commandKeyWords.contains(command)) {
                //输入了命令
                val content = when (arg) {
                    "on" -> {
                        isEnable = true
                        "我只是个没有感情的复读机"
                    }
                    "off" -> {
                        isEnable = false
                        "差不多得了😅"
                    }
                    else -> "命令无效，请输入on/off."
                }
                return ProcessResult.reply(
                    TextResponseMessage(
                        shouldQuoteReply = true,
                        content = content
                    )
                )
            }
        }
        if (isEnable) {
            //直接复读
            return ProcessResult.reply(
                TextResponseMessage(
                    shouldQuoteReply = false,
                    content = message.content
                )
            )
        }

        //交给下一个service处理
        return ProcessResult.next()
    }


}
