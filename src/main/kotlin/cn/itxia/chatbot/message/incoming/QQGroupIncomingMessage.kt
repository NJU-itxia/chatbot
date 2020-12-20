package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import net.mamoe.mirai.event.events.GroupMessageEvent

data class QQGroupIncomingMessage(
    override val content: String,
    val event: GroupMessageEvent,
) : IncomingMessage {

    override val messageFrom: MessageFrom = MessageFrom.QQ_GROUP_CHAT

    override val trackID: String
        get() = event.group.id.toString()

    val groupID = event.group.id
}
