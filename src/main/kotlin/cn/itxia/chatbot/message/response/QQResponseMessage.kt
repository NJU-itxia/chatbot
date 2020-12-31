package cn.itxia.chatbot.message.response

import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder

class QQResponseMessage(
    private vararg val messages: Message,
    override val shouldQuoteReply: Boolean = false
) : ResponseMessage {

    private val messageChain: MessageChain = MessageChainBuilder().run {
        addAll(
            messages.asList()
        )
        asMessageChain()
    }

    fun toMessageChain(): MessageChain {
        return messageChain
    }

    override fun toTextMessage(): String {
        return messageChain.contentToString()
    }

}
