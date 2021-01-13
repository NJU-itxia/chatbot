package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.util.MiraiUtil
import net.mamoe.mirai.event.events.MessageEvent

/**
 * QQ私聊.
 * */
abstract class AbstractQQPrivateIncomingMessage(
    event: MessageEvent,
) : IncomingMessage {

    final override val isExplicitCall: Boolean = true

    final override val content: String = MiraiUtil.convertMessageChainToPlainText(event.message)

    final override val trackID: String = MiraiUtil.getTrackIDFromEvent(event)

    final override val commandStyle: Command? = Command.fromText(content)
}
