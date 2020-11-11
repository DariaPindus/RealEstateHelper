package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component
public class InitBotStateHandler implements UserBotStateHandler {

    private final UserCache userCache;
    private final MessageSource messageSource;

    public InitBotStateHandler(UserCache userCache, MessageSource messageSource) {
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
        String messageText = messageSource.getMessage("bot.started.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        userCache.setUserStateFromMessage(message, BotStateEnum.STARTED);
        return responseMessage;
    }
}
