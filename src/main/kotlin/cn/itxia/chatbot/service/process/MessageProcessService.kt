package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.util.IncomingMessage
import cn.itxia.chatbot.util.ResponseMessage

interface MessageProcessService {

    /**
     * 执行的顺序, 越小越先执行.
     * */
    val order: Int
        get() = 100

    fun process(incomingMessage: IncomingMessage): IntermediaMessage

}

data class IntermediaMessage(
    /**
     * 要回复的消息.
     * null表示不回复消息.
     * */
    var responseMessage: ResponseMessage? = null,
    /**
     * 是否应该继续处理.
     * (交给下一个MessageProcessService)
     * */
    var shouldContinueProcess: Boolean = true,
)
