package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom

data class WebIncomingMessage(
    override val content: String,
    override val trackID: String
) : IncomingMessage {
    override val messageFrom: MessageFrom = MessageFrom.WEB

}
