package cn.itxia.chatbot.service.process

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
class AliveCheckService : AbstractCommandProcessService() {
    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        return isExplicitCall && CommandWords.ALIVE_CHECK.contains(commandName)
    }

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        return reply("啊，我还活着", true)
    }
}
