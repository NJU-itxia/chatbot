package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService
import cn.itxia.chatbot.util.CommandWords
import org.springframework.stereotype.Service

@Service
class HelpManualService : AbstractCommandProcessService() {

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
            buildDescription(CommandWords.HELP_MANUAL, "[命令名称]", "查询命令用法"),
            buildDescription(CommandWords.WEBSITE_LINKS, "", "常用网址目录"),
        ).joinToString("\n")
    }

    private val yuqueHelp = """
        搜索IT侠语雀文档.
        格式: doc [关键字]
        例如: doc 正版office
        alias: ${CommandWords.YUQUE_SEARCH.joinToString(",")}
    """.trimIndent()

    private val linksHelp = """
        常用网址目录.
        格式: bot links
        alias: ${CommandWords.WEBSITE_LINKS.joinToString(",")}
    """.trimIndent()

    private val helpHelp = """
        查询命令用法.
        格式: bot help [命令]
        例如: bot help doc
        alias: ${CommandWords.HELP_MANUAL.joinToString(",")}
    """.trimIndent()

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        return when (true) {
            argument == "" -> {
                ProcessResult.reply(allCommandDescription)
            }
            CommandWords.YUQUE_SEARCH.contains(argument) -> {
                ProcessResult.reply(yuqueHelp)
            }
            CommandWords.WEBSITE_LINKS.contains(argument) -> {
                ProcessResult.reply(linksHelp)
            }
            CommandWords.HELP_MANUAL.contains(argument) -> {
                ProcessResult.reply(helpHelp)
            }
            else -> {
                ProcessResult.reply("唔...还没学会这个技能")
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
