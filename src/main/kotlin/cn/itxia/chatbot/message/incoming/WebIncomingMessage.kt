package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.Command

/**
 * 来自网页端的消息.
 * */
data class WebIncomingMessage(
    override val content: String,
    override val trackID: String,
) : IncomingMessage {
    override val messageFrom: MessageFrom = MessageFrom.WEB

    override val isExplicitCall: Boolean = true

    override val commandStyle: Command? = Command.fromText(content)

}
