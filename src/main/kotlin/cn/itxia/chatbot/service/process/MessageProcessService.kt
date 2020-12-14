package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage

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
     * 例如你可判断命令开头是否是"doc"或者"语雀".
     * @param commandName 命令名称
     * @param isExplicitCall 是否是明确的调用(呼叫), QQ群里以"bot"开头或者网页端
     * @param isArgumentEmpty 参数是否为空白
     * */
    abstract fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean

    /**
     * 执行命令.
     * @param argument 参数字符串, 有可能是""(参见[shouldExecute])
     * @param isExplicitCall 是否是明确的调用(呼叫), QQ群里以"bot"开头或者网页端
     * @param message 原始消息
     * */
    abstract fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult

    /**
     * 不执行命令则会调用这个函数.
     * 默认是交给下一个service执行.
     * @param message 原始消息
     * */
    open fun onNotExecute(message: IncomingMessage): ProcessResult {
        return ProcessResult.next()
    }

    /**
     * 不要再覆盖这个方法.
     * */
    override fun process(message: IncomingMessage): ProcessResult {
        var content = message.content.trim()

        /**
         * 是否是明确的调用(呼叫).
         * 以"bot"开头, 或者是在网页端直接喊, 都算.
         * 例如QQ群里: "bot help".
         * 网页里: "help".
         * */
        var isExplicitCall = false

        if (message.messageFrom == MessageFrom.WEB) {
            isExplicitCall = true
        } else {
            if (content.startsWith("bot ")) {
                isExplicitCall = true
                //去掉前面"bot "的部分
                content = content.substring(4)
            }
        }
        if (content.isBlank()) {
            //只剩空白
            return onNotExecute(message)
        }

        //解析命令和参数
        val indexOfSpace = content.indexOf(" ")

        val isArgumentEmpty: Boolean = indexOfSpace == -1

        val command: String
        val arguments: String
        if (isArgumentEmpty) {
            command = content
            arguments = ""
        } else {
            command = content.substring(0, indexOfSpace)
            arguments = content.substring(indexOfSpace + 1)
        }

        if (shouldExecute(command, isExplicitCall, isArgumentEmpty)) {
            return executeCommand(arguments, isExplicitCall, message)
        }

        return onNotExecute(message)
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

        /**
         * 回复消息.
         * */
        fun reply(
            textResponse: String,
            quoteReply: Boolean = true,
            shouldContinueProcess: Boolean = false
        ): ProcessResult {
            return ProcessResult(
                shouldContinueProcess = shouldContinueProcess,
                response = listOf(TextResponseMessage(content = textResponse, shouldQuoteReply = quoteReply))
            )
        }

    }
}

