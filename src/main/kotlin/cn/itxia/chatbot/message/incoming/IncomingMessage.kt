package cn.itxia.chatbot.message.incoming

import cn.itxia.chatbot.enum.MessageFrom

interface IncomingMessage {

    val messageFrom: MessageFrom

    val content: String

    val trackID: String

    val isExplicitCall: Boolean

    val commandStyle: CommandStyleMessage?
}


class CommandStyleMessage private constructor(
    val commandName: String,
    val argument: String,
) {

    val isArgumentEmpty: Boolean = argument.isEmpty()

    companion object {
        fun fromText(text: String): CommandStyleMessage? {
            val command = text.trim()
            if (command.isEmpty()) {
                return null
            }
            val indexOfFirstSpace = command.indexOf(" ")
            val isArgumentEmpty = indexOfFirstSpace == -1

            return if (isArgumentEmpty) {
                CommandStyleMessage(
                    commandName = command,
                    argument = ""
                )
            } else {
                CommandStyleMessage(
                    commandName = command.substring(0, indexOfFirstSpace),
                    argument = command.substring(indexOfFirstSpace + 1)
                )
            }
        }
    }


}
