package cn.itxia.chatbot.service

import cn.itxia.chatbot.util.StorageWrapper
import cn.itxia.chatbot.util.getLogger
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MemberAuthenticationService {

    @Value("\${itxia.bot.hook.token}")
    private lateinit var token: String

    @Value("\${itxia.bot.train.auth}")
    private var enable: Boolean = false

    private val list = StorageWrapper("auth.json", object : TypeReference<MutableList<AuthenticationInfo>>() {})

    private val client = OkHttpClient()

    private val mapper = ObjectMapper().registerModule(KotlinModule())

    private val logger = getLogger()

    fun validateMemberQQ(qqID: String): Boolean {
        if (!enable) {
            return true
        }
        val authenticationInfo = list.get { it.qqID == qqID }
        if (authenticationInfo?.isItxiaMember == true) {
            return true
        }

        return validateThroughApiService(qqID)
    }

    private fun validateThroughApiService(qqID: String): Boolean {
        val request = Request.Builder()
            .url("https://nju.itxia.cn/api/member/validate/qq")
            .header("bot-token", token)
            .post(
                mapper.writeValueAsString(AuthenticationDto(qqID))
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()

        val response = client.newCall(request)
            .execute()

        val isAuthenticateSuccessful = response.isSuccessful && response.body?.string().toBoolean()
        if (isAuthenticateSuccessful) {
            logger.info("成功验证QQ号:$qqID")
            list.add(
                AuthenticationInfo(
                    qqID = qqID,
                    isItxiaMember = true
                )
            )
        }
        return isAuthenticateSuccessful
    }

}

private data class AuthenticationInfo(
    val qqID: String,
    val isItxiaMember: Boolean
)

private data class AuthenticationDto(
    val qq: String
)
