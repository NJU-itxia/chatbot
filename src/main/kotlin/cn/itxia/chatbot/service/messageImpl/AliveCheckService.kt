package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.ProcessResult.Companion.reply
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import cn.itxia.chatbot.util.CommandWords
import org.springframework.stereotype.Service

/**
 * 存活确认.
 * 看看机器人是不是挂掉了.
 */
@Service
private class AliveCheckService : AbstractCommandProcessService() {
    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && CommandWords.ALIVE_CHECK.contains(command.commandName)
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        return reply("啊，我还活着", true)
    }
}
