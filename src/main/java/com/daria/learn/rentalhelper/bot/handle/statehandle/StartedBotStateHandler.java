package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component
public class StartedBotStateHandler implements UserBotStateHandler {

    @Getter
    private static final BotStateEnum state = BotStateEnum.STARTED;

    private final MessageSource messageSource;

    public StartedBotStateHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean isApplicable(String message) {
        return message.trim().equals("/start");
    }

    @Override
    public SendMessage handleResponse(Message message) {
        String messageText = messageSource.getMessage("bot.started.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }
}
