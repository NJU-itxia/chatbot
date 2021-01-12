package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.CommandStyleMessage

interface IncomingMessage {

    val messageFrom: MessageFrom

    val content: String

    val trackID: String

    val isExplicitCall: Boolean

    val commandStyle: CommandStyleMessage?
}


