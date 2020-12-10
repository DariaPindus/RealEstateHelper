package com.daria.learn.rentalhelper.bot.handlers;

import com.daria.learn.rentalhelper.bot.exceptions.NoMatchingStateHandlersFoundException;
import com.daria.learn.rentalhelper.bot.handlers.statehandlers.*;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BotReplier {

    private final UserCache<Long> userCache;
    private final Map<BotStateEnum, UserBotStateHandler> userBotStateHandlerMap;

    public BotReplier(UserCache<Long> userCache,
                      InitBotStateHandler initBotStateHandler,
                      SelectLanguageBotStateHandler selectLanguageBotStateHandler,
                      SetPreferenceBotStateHandler setPreferenceBotStateHandler,
                      StartedBotStateHandler startedBotStateHandler) {
        this.userCache = userCache;
        userBotStateHandlerMap = Stream.of(initBotStateHandler, selectLanguageBotStateHandler, startedBotStateHandler, setPreferenceBotStateHandler)
            .collect(Collectors.toMap(UserBotStateHandler::getBotStateEnum, handler -> handler));
    }

    public SendMessage replyToMessage(Message message) {
        Long chatId = message.getChatId();
        Optional<BotStateEnum> userState = userCache.getUserState(chatId);
        BotStateEnum stateEnum = isStateSearchNeeded(message, userState) ? findApplicableBotState(message) : userState.get();
        UserBotStateHandler handler = userBotStateHandlerMap.get(stateEnum);
        if (!handler.isApplicable(message.getText()))
            throw new NoMatchingStateHandlersFoundException();
        return handler.replyToMessage(message);
    }

    private boolean isStateSearchNeeded(Message message, Optional<BotStateEnum> userState) {
        return userState.isEmpty() ||
                userBotStateHandlerMap.get(userState.get()) == null ||
                !userBotStateHandlerMap.get(userState.get()).isApplicable(message.getText());
    }

    private BotStateEnum findApplicableBotState(Message message) {
        return userBotStateHandlerMap.values().stream()
                .filter(handler -> handler.isApplicable(message.getText()))
                .findFirst()
                .map(UserBotStateHandler::getBotStateEnum)
                .orElseThrow(NoMatchingStateHandlersFoundException::new);
    }

}
