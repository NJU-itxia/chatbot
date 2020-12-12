package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.util.IncomingMessage
import cn.itxia.chatbot.util.ResponseMessage
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.util.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class YuqueDocumentSearchService : MessageProcessService {

    @Value("\${itxia.bot.yuque.token}")
    private lateinit var yuqueApiToken: String

    private val commandKeyWords = listOf("yuque", "语雀", "羽雀")

    private val client = OkHttpClient()

    override fun process(incomingMessage: IncomingMessage): IntermediaMessage {

        val (command, keyword) = incomingMessage.content.split(" ")
        if (commandKeyWords.contains(command)) {

            val url = "https://www.yuque.com/api/v2/search?type=doc&scope=itxia&q=${keyword.escapeHTML()}"

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

                    val message = if (resultCount > 0) {
                        val data = result.data[0]
                        """
                        ${data.summary.substring(0, 40)}...,
                        文档链接:https://yuque.com${data.url} ,
                        (共找到${resultCount}个结果)
                        """.trimIndent().escapeHTML()
                    } else {
                        "什么都没找到😢"
                    }


                    //回复消息
                    return IntermediaMessage(
                        responseMessage = ResponseMessage(
                            shouldResponse = true,
                            content = message,
                            shouldQuoteReply = true
                        )
                    )
                } catch (e: Exception) {
                    //TODO log this
                    e.printStackTrace()
                }

            }

        }

        return IntermediaMessage()
    }


}

private data class YuqueDocumentSearchResponse(
    val meta: Meta,
    val data: List<DataItem>
) {
    data class Meta(val total: Int)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DataItem(
        val title: String,
        val summary: String,
        val url: String
    )

}
