package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import cn.itxia.chatbot.util.CommandWords
import org.springframework.stereotype.Service

@Service
class WebsiteLinkService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && CommandWords.WEBSITE_LINKS.contains(command.commandName)
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        return ProcessResult.reply(
            """
                预约维修: https://nju.itxia.cn
                常见问题: https://www.yuque.com/itxia/help
                学校正版: http://kms.nju.edu.cn (需校园网)
                社团主页: https://itxia.club
                社团介绍: https://itxia.club/introduction
            """.trimIndent()
        )
    }
}
