package cn.itxia.chatbot.service.message

import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage

/**
 * 所有处理service的基类.
 * */
abstract class AbstractMessageProcessService {
    /**
     * 执行的顺序, 越小越先执行.
     * 如果你对执行顺序不在意，就不要覆盖修改.
     * */
    open val order: Int = 100

    /**
     * 表示是否启用该Service.
     * */
    open val isEnable: Boolean = true

    /**
     * 处理消息.
     * 请调用ProcessResult.xxx()返回处理结果.
     * */
    abstract fun process(message: IncomingMessage): ProcessResult
}
