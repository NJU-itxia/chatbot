package cn.itxia.chatbot.service.message

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.incoming.QQGroupIncomingMessage

/**
 * 处理空格分隔命令式消息.
 * 例如消息是"doc p.nju",
 * 则会调用 executeCommand("doc", "p.nju", message).
 * 如果消息不符合命令式格式, 则会调用onNotFormatCommand, 该函数默认交给下一个service执行.
 * */
abstract class AbstractCommandProcessService : AbstractMessageProcessService() {

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
     * 处理命令.
     * */
    final override fun process(message: IncomingMessage): ProcessResult {
        var content = message.content.trim()

        /**
         * 是否是明确的调用(呼叫).
         * 以"bot"开头, 或者是在网页端直接喊, 都算.
         * 例如QQ群里: "@bot help", "bot help".
         * 网页里: "help".
         * */
        var isExplicitCall = false

        if (message is QQGroupIncomingMessage && message.isAtMe) {
            isExplicitCall = true
        }

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

