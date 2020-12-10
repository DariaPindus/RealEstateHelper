package com.daria.learn.rentalhelper.bot.handlers.statehandlers;

import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserBotStateHandler {
    BotStateEnum getBotStateEnum();
    boolean isApplicable(String userResponse);
    SendMessage replyToMessage(Message message);
}
