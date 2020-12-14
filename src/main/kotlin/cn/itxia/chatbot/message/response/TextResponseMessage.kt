package cn.itxia.chatbot.message.response

data class TextResponseMessage(
    val content: String,
    override val shouldQuoteReply: Boolean = false,
) : ResponseMessage {

    override fun toTextMessage() = content

}
