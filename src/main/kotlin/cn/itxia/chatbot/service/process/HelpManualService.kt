package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.util.CommandWords
import org.springframework.stereotype.Service

@Service
class HelpManualService : CommandProcessService() {

    private val allCommandDescription: String by lazy {
        val buildDescription: (List<String>, String, String) -> String =
            fun(commandWordList, argumentDescription, commandDescription): String {
                return "${commandWordList[0]} $argumentDescription => $commandDescription (alias: ${
                    commandWordList.joinToString(
                        ","
                    )
                })"
            }

        //add your description below
        listOf(
            buildDescription(CommandWords.YUQUE_SEARCH, "[关键字]", "语雀文档搜索"),
            buildDescription(CommandWords.REPEATER, "[on/off]", "复读机"),
            buildDescription(CommandWords.HELP_MANUAL, "[命令名称]", "查询命令用法"),
        ).joinToString("\n")
    }

    private val yuqueHelp = """
        搜索IT侠语雀文档.
        格式: doc [关键字]
        例如: doc p.nju
        alias: ${CommandWords.YUQUE_SEARCH.joinToString(",")}
    """.trimIndent()

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        return when (true) {
            argument == "" -> {
                ProcessResult.reply(allCommandDescription)
            }
            CommandWords.YUQUE_SEARCH.contains(argument) -> {
                ProcessResult.reply(yuqueHelp)
            }
            else -> {
                ProcessResult.next()
            }
        }
    }

    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        if (commandName != "help") {
            return false
        }
        if (isArgumentEmpty && !isExplicitCall) {
            return false
        }
        return true
    }
}
