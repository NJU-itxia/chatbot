package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.message.Command
import net.mamoe.mirai.event.events.MessageEvent

/**
 * QQ私聊.
 * */
abstract class AbstractQQPrivateIncomingMessage(
    event: MessageEvent,
) : AbstractQQIncomingMessage(event) {

    final override val isExplicitCall: Boolean = true

    final override val commandStyle: Command? = Command.fromText(content)
}
