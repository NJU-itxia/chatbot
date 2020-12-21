package cn.itxia.chatbot.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Get logger of the class.
 * */
inline fun <reified T : Any> T.getLogger(): Logger {
    return LogManager.getLogger(T::class.java)
}
