package com.daria.learn.rentalhelper.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@AllArgsConstructor
public class BotOutgoingMessage {
    @Getter
    private final String message;
    @Getter
    private final String chatId;

    public SendMessage toTelegramMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(OfferMessage.getParseMode());
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        return sendMessage;
    }
}
