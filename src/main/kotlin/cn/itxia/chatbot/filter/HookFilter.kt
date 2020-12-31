package cn.itxia.chatbot.filter

import cn.itxia.chatbot.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
@Order(2)
class HookFilter : Filter {

    @Value("\${itxia.bot.hook.token}")
    private lateinit var token: String

    private val logger = getLogger()

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {

        val request = servletRequest as HttpServletRequest

        if (request.requestURI.startsWith("/hook")) {
            if (!validateToken(request.getHeader("bot-token"))) {
                logger.info("Token校验不通过.")
            }
        }

        chain.doFilter(servletRequest, servletResponse)
    }

    private fun validateToken(inputToken: String): Boolean {
        return token == inputToken
    }

}
