package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.util.MiraiUtil
import net.mamoe.mirai.event.events.MessageEvent

abstract class AbstractQQIncomingMessage(
    event: MessageEvent,
) : IncomingMessage {

    final override val content: String = MiraiUtil.convertMessageChainToPlainText(event.message)

    final override val trackID: String = MiraiUtil.getTrackIDFromEvent(event)

}
