package com.daria.learn.rentalhelper.bot.handlers.statehandlers;

import com.daria.learn.rentalhelper.bot.BotMessageSource;
import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component
public class InitBotStateHandler implements UserBotStateHandler {

    private final UserRepository<Long> userCache;
    private final BotMessageSource messageSource;

    public InitBotStateHandler(UserRepository<Long> userCache, BotMessageSource messageSource) {
        this.userCache = userCache;
        this.messageSource = messageSource;
    }

    @Override
    public BotStateEnum getBotStateEnum() {
        return BotStateEnum.INIT;
    }

    @Override
    public boolean isApplicable(String userResponse) {
        return userResponse.trim().equals("/start");
    }

    @Override
    public SendMessage replyToMessage(Message message) {
        String messageText = messageSource.getMessage("bot.inited.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        userCache.setUserState(message.getChatId(), BotStateEnum.SELECT_LANG);
        return responseMessage;
    }
}
