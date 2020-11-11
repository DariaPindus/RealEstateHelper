package com.daria.learn.rentalhelper.bot.handle;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotHandlerFacade {
    SendMessage handleMessageUpdate(Update update);
}
