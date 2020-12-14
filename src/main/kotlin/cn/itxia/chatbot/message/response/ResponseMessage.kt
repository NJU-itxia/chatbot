package cn.itxia.chatbot.message.response

interface ResponseMessage {

    /**
     * 回复时是否要“引用”原消息.
     * 用于QQ机器人回复时，引用原消息.
     * */
    val shouldQuoteReply: Boolean

    /**
     * 将消息转换为文字消息.
     * */
    fun toTextMessage(): String
}
