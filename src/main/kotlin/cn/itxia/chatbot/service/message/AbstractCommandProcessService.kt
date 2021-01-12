package cn.itxia.chatbot.service.message

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage

/**
 * 处理空格分隔命令式消息.
 *
 * 例如消息是"doc p.nju", 可以看作是指令"doc", 参数"p.nju".
 * 意为用关键字"p.nju"来查询文档.
 *
 * 如果消息不符合命令式格式, 则会调用onNotExecute, 该函数默认交给下一个service执行.
 * */
abstract class AbstractCommandProcessService : AbstractMessageProcessService() {

    /**
     * 判断指令是否应该执行.
     *
     * 例如你可判断指令开头是否是"doc"或者"语雀", 来决定是否查询语雀文档.
     * @param command 指令
     * @param message 原始消息
     * */
    abstract fun shouldExecute(command: Command, message: IncomingMessage): Boolean

    /**
     * 执行指令.
     * @param command 指令
     * @param message 原始消息
     * */
    abstract fun executeCommand(command: Command, message: IncomingMessage): ProcessResult

    /**
     * 不执行命令则会调用这个函数.
     *
     * 默认是交给下一个service执行.
     * @param message 原始消息
     * */
    open fun onNotExecute(message: IncomingMessage): ProcessResult {
        return ProcessResult.next()
    }

    /**
     * 处理命令.
     * */
    final override fun process(message: IncomingMessage): ProcessResult {
        val command = message.commandStyle ?: return onNotExecute(message)

        if (shouldExecute(command, message)) {
            return executeCommand(command, message)
        }

        return onNotExecute(message)
    }
}

