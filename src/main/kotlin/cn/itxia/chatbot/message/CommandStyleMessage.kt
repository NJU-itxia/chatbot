package cn.itxia.chatbot.message

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
