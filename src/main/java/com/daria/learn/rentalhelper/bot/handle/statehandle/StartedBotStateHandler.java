package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.exceptions.NoMatchingStateHandlersFoundException;
import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component
public class StartedBotStateHandler implements UserBotStateHandler {

    private final UserCache userCache;
    private final MessageSource messageSource;

    public StartedBotStateHandler(UserCache userCache, MessageSource messageSource) {
        this.userCache = userCache;
        this.messageSource = messageSource;
    }

    @Override
    public BotStateEnum getBotStateEnum() {
        return BotStateEnum.STARTED;
    }

    @Override
    public boolean isApplicable(String userResponse) {
        String messageFormatted = userResponse.toLowerCase().trim();
        return messageFormatted.equals("начать") || messageFormatted.equals("настроить");
    }

    @Override
    public SendMessage replyToMessage(Message message) {
        String messageFormatted = message.getText().toLowerCase().trim();
        switch (messageFormatted) {
            case "начать": return replyWithSubscription(message);
            case "настроить": return replyWithPreferences(message);
            default: throw new NoMatchingStateHandlersFoundException();
        }
    }

    private SendMessage replyWithPreferences(Message message) {
        userCache.setUserStateFromMessage(message, BotStateEnum.SET_PREFERENCES);
        String messageText = messageSource.getMessage("bot.set-preferences.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

    private SendMessage replyWithSubscription(Message message) {
        userCache.setUserStateFromMessage(message, BotStateEnum.SUBSCRIBED);
        String messageText = messageSource.getMessage("bot.subscribed.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

}
