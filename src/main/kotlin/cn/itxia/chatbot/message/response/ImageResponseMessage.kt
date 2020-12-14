package cn.itxia.chatbot.message.response

import java.io.File

data class ImageResponseMessage(
    val image: File,
    override val shouldQuoteReply: Boolean = false
) : ResponseMessage {
    override fun toTextMessage() = "[图片]"
}
