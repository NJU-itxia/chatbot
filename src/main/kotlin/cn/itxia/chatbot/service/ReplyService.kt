package cn.itxia.chatbot.service

import cn.itxia.chatbot.service.process.MessageProcessService
import cn.itxia.chatbot.util.IncomingMessage
import cn.itxia.chatbot.util.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ReplyService {

    private val messageProcessServiceList: MutableList<MessageProcessService> = mutableListOf()

    fun replyMessage(incomingMessage: IncomingMessage): ResponseMessage {
        //交给service处理消息
        for (messageProcessService in messageProcessServiceList) {
            val intermedia = messageProcessService.process(incomingMessage)
            if (intermedia.responseMessage != null) {
                //回复已经处理好的消息
                return intermedia.responseMessage!!
            }
            if (!intermedia.shouldContinueProcess) {
                break
            }
        }

        //不返回消息(这个部分设计可能不太好)
        return ResponseMessage(
            shouldResponse = false,
            content = "",
            shouldQuoteReply = false
        )
    }

    fun registerMessageProcessService(messageProcessService: MessageProcessService) {
        messageProcessServiceList.add(messageProcessService)
        //按照优先级排序
        messageProcessServiceList.sortBy { it.order }
    }

}

@Component
private class ProcessServiceAutoRegister : BeanPostProcessor {

    @Autowired
    private lateinit var replyService: ReplyService

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        //MessageProcessService初始化时,注册到replyService
        if (bean is MessageProcessService) {
            replyService.registerMessageProcessService(bean)
        }
        return super.postProcessAfterInitialization(bean, beanName)
    }
}
