package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.enum.ProcessPriority
import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import cn.itxia.chatbot.util.CommandWords
import org.springframework.stereotype.Service

/**
 * 复读机.
 * */
@Service
private class RepeaterService : AbstractCommandProcessService() {

    override val priority = ProcessPriority.REPEATER

    override val isEnable: Boolean = false

    private var isRepeaterEnable = false

    private val commandKeyWords = CommandWords.REPEATER

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return !command.isArgumentEmpty && commandKeyWords.contains(command.commandName)
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        val content = when (command.argument) {
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
        //直接复读
        return ProcessResult.reply(content)
    }

    override fun onNotExecute(message: IncomingMessage): ProcessResult {
        if (isRepeaterEnable) {
            //直接复读
            return ProcessResult.reply(
                textResponse = message.content,
                quoteReply = false
            )
        }

        return super.onNotExecute(message)
    }
}
