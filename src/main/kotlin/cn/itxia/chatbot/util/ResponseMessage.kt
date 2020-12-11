package cn.itxia.chatbot.util

data class ResponseMessage(

    /**
     * 是否应该回复消息.
     * 例如在QQ群里，不是所有消息都需要回复的.
     * 连续的命令输入可能也不需要回复.
     * */
    val shouldResponse: Boolean,

    /**
     * 回复消息的内容.
     * */
    val content: String = "",

    /**
     * 回复时是否要“引用”原消息.
     * 用于QQ机器人回复时，引用原消息.
     * */
    val shouldQuoteReply: Boolean = false

)
