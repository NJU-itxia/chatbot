package cn.itxia.chatbot.service.process;

import cn.itxia.chatbot.message.incoming.IncomingMessage;
import cn.itxia.chatbot.util.CommandWords;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * 存活确认.
 * 看看机器人是不是挂掉了.
 * (使用Java编写的demo)
 */
@Service
public class AliveCheckService extends CommandProcessService {

    @Override
    public boolean shouldExecute(@NotNull String commandName, boolean isExplicitCall, boolean isArgumentEmpty) {
        return CommandWords.Companion.getALIVE_CHECK().contains(commandName);
    }

    @NotNull
    @Override
    public ProcessResult executeCommand(@NotNull String argument, boolean isExplicitCall, @NotNull IncomingMessage message) {
        return ProcessResult.Companion.reply("啊，我还活着", true, true);
    }
}
