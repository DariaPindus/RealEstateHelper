package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserBotStateHandler {
    BotStateEnum getBotStateEnum();
    boolean isApplicable(String userResponse);
    SendMessage replyToMessage(Message message);
}
