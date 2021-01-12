package cn.itxia.chatbot.message

import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.message.response.TextResponseMessage

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
