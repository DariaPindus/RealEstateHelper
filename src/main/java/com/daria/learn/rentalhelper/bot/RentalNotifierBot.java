package com.daria.learn.rentalhelper.bot;

import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;

import java.util.List;

public interface RentalNotifierBot {
    void sendMessagesToUsers(List<BotOutgoingMessage> messageList);
}
