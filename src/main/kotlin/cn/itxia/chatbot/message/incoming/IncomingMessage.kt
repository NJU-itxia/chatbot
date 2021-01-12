package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.message.CommandStyleMessage

/**
 * 所有类型消息的抽象接口.
 * */
interface IncomingMessage {

    /**
     * 消息的来源.
     * 如QQ群/QQ私聊/网页.
     * */
    val messageFrom: MessageFrom

    /**
     * 消息的纯文本内容.
     * */
    val content: String

    /**
     * 跟踪ID.
     * 用于跟踪连续的对话.
     * */
    val trackID: String

    /**
     * 是否在(明确地)叫机器人.
     *
     * 这是为了在QQ群聊天中, 区分出bot要回复的内容.
     * (并不是所有消息bot都要回应，防止错误触发bot)
     *
     * QQ群里, 只有以"bot "开头，或者@bot, 才算是explicit call.
     * */
    val isExplicitCall: Boolean

    /**
     * 转换成指令形式.
     *
     * 如收到消息"bot doc 正版", 将转换为指令"doc"和参数"正版".
     * */
    val commandStyle: CommandStyleMessage?
}


