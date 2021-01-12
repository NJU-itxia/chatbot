package cn.itxia.chatbot.enum

private const val BASE_PRIORITY = 100

private const val KEYWORD_SERVICE_BASE_PRIORITY = 65535

/**
 * 执行优先级.
 *
 * 越小越先执行.
 * */
enum class ProcessPriority(val priority: Int) {

    DEFAULT(BASE_PRIORITY),

    REPEATER(64),

    KEYWORD_LEARN(KEYWORD_SERVICE_BASE_PRIORITY + 1),

    KEYWORD_FORGET(KEYWORD_SERVICE_BASE_PRIORITY + 2),

    KEYWORD_REPLY(KEYWORD_SERVICE_BASE_PRIORITY + 3),

    ;
}
