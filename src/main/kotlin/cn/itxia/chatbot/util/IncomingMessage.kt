package cn.itxia.chatbot.util

import cn.itxia.chatbot.enum.MessageFrom
import net.mamoe.mirai.message.GroupMessageEvent

data class IncomingMessage(

    /**
     * 用于标识发送人.
     * 持续跟踪对话时，需要用到这个.(如连续的问答)
     * */
    val senderID: String,

    /**
     * 消息从哪个平台发来.
     * */
    val messageFrom: MessageFrom,

    /**
     * 消息内容.
     * plain text.
     * */
    val content: String,

    /**
     * 从QQ群接受消息时的event.
     * */
    val qqGroupMessageEvent: GroupMessageEvent? = null

)
