package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import cn.itxia.chatbot.util.CommandWords
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.util.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * 搜索语雀文档.
 * */
@Service
class YuqueDocumentSearchService : CommandProcessService() {

    @Value("\${itxia.bot.yuque.token}")
    private lateinit var yuqueApiToken: String

    private val commandKeyWords = CommandWords.YUQUE_SEARCH

    private val client = OkHttpClient()

    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        return !isArgumentEmpty && commandKeyWords.contains(commandName)
    }

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        val url = "https://www.yuque.com/api/v2/search?type=doc&scope=itxia&q=${argument.escapeHTML()}"

        val request = Request.Builder()
            .url(url)
            .get()
            .header("X-Auth-Token", yuqueApiToken)
            .build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful) {
                //TODO log this error
                print("Failed to search yuque doc.")
            }
            try {
                val body = it.body!!.string()
                val result = ObjectMapper().registerModule(KotlinModule())
                    .readValue(body, YuqueDocumentSearchResponse::class.java)

                val resultCount = result.meta.total

                val responseMessage = if (resultCount > 0) {
                    val data = result.data[0]
                    """
                        ${data.summary.substring(0, 40)}...,
                        文档链接:https://yuque.com${data.url} ,
                        (共找到${resultCount}个结果)
                        """.trimIndent()
                } else {
                    "什么都没找到😢"
                }

                //回复消息
                return ProcessResult.reply(
                    TextResponseMessage(
                        content = responseMessage,
                        shouldQuoteReply = true
                    )
                )
            } catch (e: Exception) {
                //TODO log this
                e.printStackTrace()
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
