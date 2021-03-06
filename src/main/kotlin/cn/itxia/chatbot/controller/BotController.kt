package cn.itxia.chatbot.controller

import cn.itxia.chatbot.dto.ChatMessageDto
import cn.itxia.chatbot.message.incoming.WebIncomingMessage
import cn.itxia.chatbot.service.ReplyService
import cn.itxia.chatbot.vo.ChatResponseVo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class BotController {

    @Autowired
    private lateinit var replyService: ReplyService

    @PostMapping("/chat")
    fun chat(@RequestBody dto: ChatMessageDto): ChatResponseVo {
        //TODO 为没有cookie session的用户分配一个ID，并返回cookie

        //TODO 从cookie中获取用户对应的ID
        val id = ""

        val response = replyService.replyMessage(
            WebIncomingMessage(
                content = dto.content,
                trackID = id
            )
        )

        return ChatResponseVo(
            contentList = response.map { it.toTextMessage() },
            date = Date()
        )
    }


}
