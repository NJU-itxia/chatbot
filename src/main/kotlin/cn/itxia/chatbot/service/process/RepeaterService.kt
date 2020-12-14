package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import org.springframework.stereotype.Service

/**
 * 复读机.
 * */
@Service
class RepeaterService : CommandProcessService() {

    override val order: Int = 64

    private var isRepeaterEnable = false

    private val commandKeyWords = listOf("复读", "复读机")

    override fun shouldExecute(commandName: String): Boolean {
        return commandKeyWords.contains(commandName)
    }

    override fun executeCommand(argument: String, message: IncomingMessage): ProcessResult {
        val content = when (argument) {
            "on" -> {
                isRepeaterEnable = true
                "我只是个没有感情的复读机"
            }
            "off" -> {
                isRepeaterEnable = false
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

    override fun onNotACommand(message: IncomingMessage): ProcessResult {
        if (isRepeaterEnable) {
            //直接复读
            return ProcessResult.reply(
                TextResponseMessage(
                    shouldQuoteReply = false,
                    content = message.content
                )
            )
        }

        return super.onNotACommand(message)
    }
}
