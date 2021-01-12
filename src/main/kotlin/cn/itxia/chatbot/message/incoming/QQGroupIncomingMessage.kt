package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.CommandStyleMessage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText

data class QQGroupIncomingMessage(
    val event: GroupMessageEvent
) : IncomingMessage {

    override val messageFrom: MessageFrom = MessageFrom.QQ_GROUP_CHAT

    override val content: String =
        event.message.filterIsInstance<PlainText>().joinToString(separator = "") { it.contentToString() }

    override val trackID: String = event.group.id.toString()

    val isAtMe: Boolean = event.message.any {
        it is At && it.target == event.bot.id
    }

    private val isStartWithBot: Boolean = content.startsWith("bot ")

    override val isExplicitCall: Boolean = isAtMe || isStartWithBot

    override val commandStyle: CommandStyleMessage? =
        if (isStartWithBot) {
            CommandStyleMessage.fromText(content.removePrefix("bot "))
        } else {
            CommandStyleMessage.fromText(content)
        }

    val groupID = event.group.id
}
