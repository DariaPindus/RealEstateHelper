package com.daria.learn.rentalhelper.bot.handle;

import com.daria.learn.rentalhelper.bot.exceptions.NoMatchingStateHandlersFoundException;
import com.daria.learn.rentalhelper.bot.handle.statehandle.InitBotStateHandler;
import com.daria.learn.rentalhelper.bot.handle.statehandle.SetPreferenceBotStateHandler;
import com.daria.learn.rentalhelper.bot.handle.statehandle.StartedBotStateHandler;
import com.daria.learn.rentalhelper.bot.handle.statehandle.UserBotStateHandler;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BotReplier {

    private final UserCache userCache;
    private final Map<BotStateEnum, UserBotStateHandler> userBotStateHandlerMap;

    public BotReplier(UserCache userCache,
                      InitBotStateHandler initBotStateHandler,
                      SetPreferenceBotStateHandler setPreferenceBotStateHandler,
                      StartedBotStateHandler startedBotStateHandler) {
        this.userCache = userCache;
        userBotStateHandlerMap = Stream.of(initBotStateHandler, startedBotStateHandler, setPreferenceBotStateHandler)
            .collect(Collectors.toMap(UserBotStateHandler::getBotStateEnum, handler -> handler));
    }

    public SendMessage replyToMessage(Message message) {
        Integer userId = message.getFrom().getId();
        BotStateEnum userState = userCache.getUserState(userId).orElse(findApplicableBotState(message));
        UserBotStateHandler handler = userBotStateHandlerMap.get(userState);
        if (!handler.isApplicable(message.getText()))
            throw new NoMatchingStateHandlersFoundException();
        return handler.replyToMessage(message);
    }

    private BotStateEnum findApplicableBotState(Message message) {
        return userBotStateHandlerMap.values().stream()
                .filter(handler -> handler.isApplicable(message.getText()))
                .findFirst()
                .map(UserBotStateHandler::getBotStateEnum)
                .orElseThrow(NoMatchingStateHandlersFoundException::new);
    }

}
