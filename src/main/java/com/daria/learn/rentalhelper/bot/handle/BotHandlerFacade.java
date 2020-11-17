package com.daria.learn.rentalhelper.bot.handle;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface BotHandlerFacade {
    SendMessage handleMessageUpdate(Update update);
}
