package cn.itxia.chatbot.service

import cn.itxia.chatbot.util.IncomingMessage
import cn.itxia.chatbot.util.ResponseMessage
import org.springframework.stereotype.Service

@Service
class ReplyService {

    fun replyMessage(incomingMessage: IncomingMessage): ResponseMessage {
        //TODO 处理消息

        return ResponseMessage(
            shouldResponse = true,
            //复读机，直接返回一样的消息
            content = incomingMessage.content,
            shouldQuoteReply = false
        )

    }

}
