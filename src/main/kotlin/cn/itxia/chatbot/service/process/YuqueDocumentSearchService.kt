package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.util.CommandWords
import cn.itxia.chatbot.util.getLogger
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.util.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder

/**
 * 搜索语雀文档.
 * */
@Service
class YuqueDocumentSearchService : CommandProcessService() {

    @Value("\${itxia.bot.yuque.token}")
    private lateinit var yuqueApiToken: String

    private val commandKeyWords = CommandWords.YUQUE_SEARCH

    private val client = OkHttpClient()

    private val logger = getLogger()

    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        return !isArgumentEmpty && commandKeyWords.contains(commandName)
    }

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        val escapedKeyword = URLEncoder.encode(argument.escapeHTML(), "utf-8")

        val url = "https://www.yuque.com/api/v2/search?type=doc&scope=itxia&q=${escapedKeyword}"

        logger.info("查询语雀文档，关键字:($argument)")

        val request = Request.Builder()
            .url(url)
            .get()
            .header("X-Auth-Token", yuqueApiToken)
            .build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful) {
                logger.error("查询语雀文档失败, status code:${it.code}")
            }
            try {
                val body = it.body!!.string()
                val result = ObjectMapper().registerModule(KotlinModule())
                    .readValue(body, YuqueDocumentSearchResponse::class.java)

                val resultCount = result.meta.total

                val responseMessage = if (resultCount > 0) {
                    //返回前三个结果
                    val maxLength = result.data.size.coerceAtMost(3)
                    result.data.subList(0, maxLength)
                        .joinToString("\n------------\n") { dataItem ->
                            """
                        ${dataItem.summary.replace(Regex("\\<\\/?\\w+\\>"), "").substring(0, 45)}...,
                        链接:https://yuque.com${dataItem.url}
                        """.trimIndent()
                        } + "\n------------\n共找到${resultCount}个结果,详见\nhttps://www.yuque.com/itxia/s?q=${escapedKeyword}"
                } else {
                    "什么都没找到😢"
                }
                logger.info("查询语雀文档找到${resultCount}个结果.")

                //回复消息
                return ProcessResult.reply(
                    textResponse = responseMessage,
                    quoteReply = true
                )
            } catch (e: Exception) {
                logger.error("查询语雀文档失败, ${e.message}")
                return ProcessResult.stop()
            }
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class YuqueDocumentSearchResponse(
    val meta: Meta,
    val data: List<DataItem>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Meta(val total: Int)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DataItem(
        val title: String,
        val summary: String,
        val url: String
    )

}
