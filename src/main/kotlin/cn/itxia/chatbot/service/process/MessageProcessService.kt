package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.ResponseMessage

interface MessageProcessService {
    /**
     * 执行的顺序, 越小越先执行.
     * 如果你对执行顺序不在意，就不要覆盖修改.
     * */
    val order: Int
        get() = 100

    /**
     * 处理消息.
     *
     * */
    fun process(message: IncomingMessage): ProcessResult

}

class ProcessResult private constructor(
    val shouldContinueProcess: Boolean,
    val response: List<ResponseMessage> = listOf()
) {

    companion object {
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

