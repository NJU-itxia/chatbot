package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import org.springframework.stereotype.Service

@Service
class TrashMessageService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && command.commandName == "废话"
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        val template = """
            0是怎么回事呢？下面就让小编带大家一起了解吧。
            0，其实就是0了，那么为什么0，相信大家都很好奇是怎么回事。
            大家可能会感到很惊讶，0究竟是为什么呢？但事实就是这样，小编也感到非常惊讶。
            那么这就是关于0的事情了，大家有没有觉得很神奇呢？看了今天的内容，大家有什么想法呢？欢迎回复一起讨论噢~
        """.trimIndent()

        val actualArgument = if (command.isArgumentEmpty) {
            command.argument
        } else {
            "说废话"
        }

        val trash = template.replace("0", actualArgument)

        return ProcessResult.reply(trash)
    }
}
