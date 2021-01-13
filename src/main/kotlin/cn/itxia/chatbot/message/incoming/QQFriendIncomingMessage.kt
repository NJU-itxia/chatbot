package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import net.mamoe.mirai.event.events.FriendMessageEvent

/**
 * 来自QQ好友的消息.
 * */
data class QQFriendIncomingMessage(
    val event: FriendMessageEvent,
) : AbstractQQPrivateIncomingMessage(event) {

    override val messageFrom: MessageFrom = MessageFrom.QQ_FRIEND_CHAT

}
