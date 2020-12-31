package cn.itxia.chatbot.controller

import cn.itxia.chatbot.dto.NewOrderHookDto
import cn.itxia.chatbot.message.response.QQResponseMessage
import cn.itxia.chatbot.service.MiraiQQRobotService
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HookController {

    @Autowired
    private lateinit var miraiQQRobotService: MiraiQQRobotService

    //监听消息的群
    private val xianlinGroups = listOf<Long>()
    private val gulouGroups = listOf<Long>()
    private val otherGroups = listOf<Long>(766616165)

    @PostMapping("/hook/newOrder")
    fun newOrderHook(@RequestBody dto: NewOrderHookDto) {

        val description = dto.description.replace(Regex("\\n{2,}"), "\n")

        val message = QQResponseMessage(
            PlainText("${dto.campus}校区的${dto.name}发来预约,问题描述如下:\n${description}\n详情请到预约系统查看~"),
            Face(Face.ZHENG_YAN)
        )

        when (dto.campus) {
            "鼓楼" -> {
                miraiQQRobotService.sendToGroups(gulouGroups, message)
            }
            "仙林" -> {
                miraiQQRobotService.sendToGroups(xianlinGroups, message)
            }
            else -> {
            }
        }
        miraiQQRobotService.sendToGroups(otherGroups, message)

    }

}
