package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.service.process.MessageProcessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ReplyService {

    private val messageProcessServiceList: MutableList<MessageProcessService> = mutableListOf()

    fun replyMessage(incomingMessage: IncomingMessage): List<ResponseMessage> {

        val responseMessageList: MutableList<ResponseMessage> = mutableListOf()

        //交给service处理消息
        for (messageProcessService in messageProcessServiceList) {
            val processResult = messageProcessService.process(incomingMessage)
            responseMessageList.addAll(processResult.response)
            if (!processResult.shouldContinueProcess) {
                break
            }
        }

        //不返回消息(这个部分设计可能不太好)
        return responseMessageList
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
        if (bean is MessageProcessService && bean.isEnable) {
            replyService.registerMessageProcessService(bean)
        }
        return super.postProcessAfterInitialization(bean, beanName)
    }
}
