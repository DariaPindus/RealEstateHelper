package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component
public class SubscribeBotStateHandler implements UserBotStateHandler {

    private final UserCache userCache;
    private final MessageSource messageSource;

    public SubscribeBotStateHandler(UserCache userCache, MessageSource messageSource) {
        this.userCache = userCache;
        this.messageSource = messageSource;
    }

    @Override
    public boolean isApplicable(String message) {
        return message.toLowerCase().trim().equals("начать");
    }

    @Override
    public SendMessage handleResponse(Message message) {
        Integer userId = getUserId(message);
        userCache.setUserState(userId, BotStateEnum.SUBSCRIBED);
        String messageText = messageSource.getMessage("bot.subscribed.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

    private Integer getUserId(Message message) {
        return message.getFrom().getId();
    }
}
