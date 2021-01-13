package cn.itxia.chatbot.util

import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.TempMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText

class MiraiUtil {

    companion object {

        fun convertMessageChainToPlainText(message: MessageChain): String {
            return message.filterIsInstance<PlainText>()
                .joinToString(separator = "") { it.contentToString() }
                .trim()
        }

        fun getTrackIDFromEvent(event: MessageEvent): String {
            return when (event) {
                is GroupMessageEvent -> {
                    "gm-${event.group.id}-${event.sender.id}"
                }
                is TempMessageEvent -> {
                    "gt-${event.group.id}-${event.sender.id}"
                }
                is FriendMessageEvent -> {
                    "fm-${event.sender.id}"
                }
                else -> {
                    "unknown"
                }
            }
        }

        fun isAtMe(event: GroupMessageEvent): Boolean {
            return event.message.any() {
                it is At && it.target == event.bot.id
            }
        }

    }
}
