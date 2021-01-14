package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import net.mamoe.mirai.event.events.GroupTempMessageEvent

/**
 * 来自群临时会话的消息.
 * */
data class QQTempIncomingMessage(
    val event: GroupTempMessageEvent,
) : AbstractQQPrivateIncomingMessage(event) {

    override val messageFrom: MessageFrom = MessageFrom.QQ_TEMP_CHAT

    /**
     * 来自哪个群的群号.
     * */
    val fromGroupID = event.group.id
}
