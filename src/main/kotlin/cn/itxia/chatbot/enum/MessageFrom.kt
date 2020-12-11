package cn.itxia.chatbot.enum

enum class MessageFrom {
    /**
     * 网页端.
     * (其实是HTTP端)
     * */
    WEB,

    /**
     * QQ群聊.
     * */
    QQ_GROUP_CHAT,

    /**
     * QQ一对一私聊.
     * */
    QQ_FRIEND_CHAT
}
