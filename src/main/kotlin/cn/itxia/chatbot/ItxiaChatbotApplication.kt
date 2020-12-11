package cn.itxia.chatbot

import cn.itxia.chatbot.service.MiraiQQRobotService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class ItxiaChatbotApplication

fun main(args: Array<String>) {
    runApplication<ItxiaChatbotApplication>(*args)
}

@Component
private class MiraiQQBotRunner : ApplicationRunner {

    @Autowired
    private lateinit var miraiQQRobotService: MiraiQQRobotService

    override fun run(args: ApplicationArguments?) {
        //启动QQ机器人
        GlobalScope.launch {
            miraiQQRobotService.startMiraiBot()
        }
    }

}
