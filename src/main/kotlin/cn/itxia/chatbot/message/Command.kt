package cn.itxia.chatbot.message

/**
 * 指令.
 * */
class Command private constructor(
    val commandName: String,
    val argument: String,
) {

    /**
     * 参数是否为空.
     * (0长度，或者全为空白)
     * */
    val isArgumentEmpty: Boolean = argument.isEmpty()

    companion object {
        /**
         * 从文本中解析出指令.
         * */
        fun fromText(text: String): Command? {
            val command = text.trim()
            if (command.isEmpty()) {
                return null
            }
            val indexOfFirstSpace = command.indexOf(" ")
            val isArgumentEmpty = indexOfFirstSpace == -1

            return if (isArgumentEmpty) {
                Command(
                    commandName = command,
                    argument = ""
                )
            } else {
                Command(
                    commandName = command.substring(0, indexOfFirstSpace),
                    argument = command.substring(indexOfFirstSpace + 1)
                )
            }
        }
    }

}
