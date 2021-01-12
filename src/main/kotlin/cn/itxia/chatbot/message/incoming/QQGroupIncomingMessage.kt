package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.Command
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText

/**
 * 来自QQ群的消息.
 * */
data class QQGroupIncomingMessage(
    val event: GroupMessageEvent
) : IncomingMessage {

    override val messageFrom: MessageFrom = MessageFrom.QQ_GROUP_CHAT

    override val content: String =
        event.message.filterIsInstance<PlainText>().joinToString(separator = "") { it.contentToString() }

    override val trackID: String = event.group.id.toString()

    /**
     * 是否在@机器人.
     *
     * 要判断是否在叫机器人, 请使用isExplicitCall.
     * @see isExplicitCall
     * */
    val isAtMe: Boolean = event.message.any {
        it is At && it.target == event.bot.id
    }

    /**
     * 消息是否以"bot "开头.
     *
     * 这是叫机器人的第二种方法.
     * */
    private val isStartWithBot: Boolean = content.startsWith("bot ")

    override val isExplicitCall: Boolean = isAtMe || isStartWithBot

    override val commandStyle: Command? =
        if (isStartWithBot) {
            Command.fromText(content.removePrefix("bot "))
        } else {
            Command.fromText(content)
        }

    /**
     * QQ群号.
     * */
    val groupID = event.group.id
}
