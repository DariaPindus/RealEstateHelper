package com.daria.learn.rentalhelper.bot.handle.statehandle;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component("defaultBotStateHandler")
public class DefaultBotStateHandler implements UserBotStateHandler {
    private final MessageSource messageSource;

    public DefaultBotStateHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean isApplicable(String message) {
        return false;
    }

    @Override
    public SendMessage handleResponse(Message message) {
        String messageText = messageSource.getMessage("bot.default.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }
}
