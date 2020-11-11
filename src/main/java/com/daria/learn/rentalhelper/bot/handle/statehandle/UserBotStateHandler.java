package com.daria.learn.rentalhelper.bot.handle.statehandle;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface wUserBotStateHandler {
    boolean isApplicable(String message);
    SendMessage handleResponse(Message message);
}
