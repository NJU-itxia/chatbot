package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom

interface IncomingMessage {

    val messageFrom: MessageFrom

    val content: String

    val trackID: String
}
