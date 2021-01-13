package cn.itxia.chatbot.service.message

import cn.itxia.chatbot.enum.MessageFrom
import cn.itxia.chatbot.enum.ProcessPriority
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage

/**
 * 所有处理service的基类.
 * */
abstract class AbstractMessageProcessService {
    /**
     * 执行的顺序.
     *
     * 如果你对执行顺序不在意，就不要覆盖修改.
     *
     * @see ProcessPriority
     * */
    open val priority: ProcessPriority = ProcessPriority.DEFAULT

    /**
     * 表示是否启用该Service.
     * */
    open val isEnable: Boolean = true

    /**
     * 接收的消息类型.
     *
     * 默认接收全部类型的消息.
     * */
    open val acceptMessageFrom: List<MessageFrom> = MessageFrom.values().toList()

    /**
     * 处理消息.
     * 请调用ProcessResult.xxx()返回处理结果.
     * */
    abstract fun process(message: IncomingMessage): ProcessResult
}
