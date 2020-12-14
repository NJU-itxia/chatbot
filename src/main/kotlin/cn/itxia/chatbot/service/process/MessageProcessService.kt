package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.ResponseMessage

/**
 * 所有处理service的基类.
 * */
abstract class MessageProcessService {
    /**
     * 执行的顺序, 越小越先执行.
     * 如果你对执行顺序不在意，就不要覆盖修改.
     * */
    open val order: Int = 100

    /**
     * 表示是否启用该Service.
     * */
    open val isEnable: Boolean = true

    /**
     * 处理消息.
     * 请调用ProcessResult.xxx()返回处理结果.
     * */
    abstract fun process(message: IncomingMessage): ProcessResult
}

/**
 * 处理空格分隔命令式消息.
 * 例如消息是"doc p.nju",
 * 则会调用 executeCommand("doc", "p.nju", message).
 * 如果消息不符合命令式格式, 则会调用onNotFormatCommand, 该函数默认交给下一个service执行.
 * */
abstract class CommandProcessService : MessageProcessService() {

    /**
     * 判断命令是否应该执行.
     * 例如判断命令开头是否是"doc"或者"语雀".
     * */
    abstract fun shouldExecute(commandName: String): Boolean

    /**
     * 执行命令.
     * */
    abstract fun executeCommand(argument: String, message: IncomingMessage): ProcessResult

    /**
     * 当消息不符合命令式格式时调用.
     * 默认是交给下一个service执行.
     * */
    open fun onNotACommand(message: IncomingMessage): ProcessResult {
        return ProcessResult.next()
    }

    /**
     * 不要再覆盖这个方法.
     * */
    override fun process(message: IncomingMessage): ProcessResult {
        val split = message.content.split(" ")
        if (split.size == 2) {
            return onNotACommand(message)
        }
        if (shouldExecute(split[0])) {
            return executeCommand(split[1], message)
        }
        return ProcessResult.next()
    }
}

class ProcessResult private constructor(
    val shouldContinueProcess: Boolean,
    val response: List<ResponseMessage> = listOf()
) {
    companion object {
        /**
         * 不回复消息，交由下一个service处理.
         * */
        fun next(): ProcessResult {
            return ProcessResult(shouldContinueProcess = true)
        }

        /**
         * 回复消息,并且不再执行后面的处理.
         * */
        fun reply(vararg response: ResponseMessage): ProcessResult {
            return ProcessResult(
                shouldContinueProcess = false,
                response = response.toList()
            )
        }

        /**
         * 回复消息,但会继续执行后面的处理.
         * */
        fun replyAndContinue(vararg response: ResponseMessage): ProcessResult {
            return ProcessResult(
                shouldContinueProcess = true,
                response = response.toList()
            )
        }

        /**
         * 停止处理,不再执行后续的处理.
         * */
        fun stop(): ProcessResult {
            return ProcessResult(shouldContinueProcess = false)
        }
    }
}

