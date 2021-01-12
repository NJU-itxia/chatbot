package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.ProcessResult.Companion.reply
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import kotlin.math.floor

/**
 * 抛骰子.
 * This is just a demo.
 * */
//@Service
class DiceService : AbstractCommandProcessService() {

    override val isEnable: Boolean = false

    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        return commandName == "dice"
    }

    private fun onceDice(): Int {
        return floor(Math.random() * 5 + 1).toInt()
    }

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        if (argument == "") {
            //抛一次
            val result = onceDice()
            val content = if (result < 5) {
                "唔...这次抛了" + result + "点"
            } else {
                "手气不错," + result + "点"
            }
            return reply(content)
        } else {
            val count = argument.toInt()
            val sum = 0
            //TODO 抛多次
            return reply(
                TextResponseMessage(
                    "让我来抛" + count + "次",
                    true
                ),
                TextResponseMessage(
                    "一共" + sum + "点",
                    true
                )
            )
        }
    }
}
