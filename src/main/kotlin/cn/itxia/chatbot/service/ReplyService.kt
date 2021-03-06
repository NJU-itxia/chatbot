package cn.itxia.chatbot.service

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.ResponseMessage
import cn.itxia.chatbot.service.message.AbstractMessageProcessService
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Service

@Service
class ReplyService : BeanPostProcessor {

    private val messageProcessServiceList: MutableList<AbstractMessageProcessService> = mutableListOf()

    /**
     * 处理发来的消息.
     * 有回复时调用reply函数返回消息.
     *
     * 为什么不直接return，而是增加一个reply函数？
     * 因为有些消息处理可能需要较长时间，如果等待所有消息再return会造成延迟.
     * 直接调用reply函数则可以及时返回消息.
     * (当然，你还可以选择另一个return MessageList的overload)
     *
     * @param incomingMessage 发来的消息
     * @param reply 回复的回调
     * */
    fun replyMessage(incomingMessage: IncomingMessage, reply: (ResponseMessage) -> Unit) {
        for (messageProcessService in messageProcessServiceList) {
            //如果service不接收此类型消息,则跳过
            if (!messageProcessService.acceptMessageFrom.contains(incomingMessage.messageFrom)) {
                continue
            }

            //交给service处理消息
            val result = messageProcessService.process(incomingMessage)

            //回复消息
            result.response.forEach { reply(it) }

            //判断是否该执行后续的service
            if (!result.shouldContinueProcess) {
                break
            }
        }
    }


    /**
     * 上一个方法的overload.
     *
     * @param incomingMessage 发来的消息
     * @return 回复消息的list
     * */
    fun replyMessage(incomingMessage: IncomingMessage): List<ResponseMessage> {
        val responseMessageList: MutableList<ResponseMessage> = mutableListOf()

        val collectToList: (ResponseMessage) -> Unit = fun(responseMessage: ResponseMessage) {
            responseMessageList.add(responseMessage)
        }

        replyMessage(incomingMessage, collectToList)

        return responseMessageList
    }

    private fun registerMessageProcessService(messageProcessService: AbstractMessageProcessService) {
        messageProcessServiceList.add(messageProcessService)
        //按照优先级排序
        messageProcessServiceList.sortBy { it.priority.priority }
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        //AbstractMessageProcessService初始化时,注册到replyService
        if (bean is AbstractMessageProcessService && bean.isEnable) {
            registerMessageProcessService(bean)
        }
        return super.postProcessAfterInitialization(bean, beanName)
    }

}

