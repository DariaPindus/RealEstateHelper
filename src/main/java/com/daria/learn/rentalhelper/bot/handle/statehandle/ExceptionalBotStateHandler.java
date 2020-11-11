package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.exceptions.BotException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component
public class ExceptionalBotStateHandler {
    private final MessageSource messageSource;

    public ExceptionalBotStateHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public SendMessage handleExceptionalResponse(Message message, BotException exception) {
        String sourceString = exception.getExceptionMessageSource();
        String messageText = messageSource.getMessage(sourceString, null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }
}
