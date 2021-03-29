package com.daria.learn.rentalhelper.bot.notification;

import com.daria.learn.rentalhelper.dtos.RentalOffersListDTO;

public interface RentalBotNotifierFacade {
    void notifySubscribedUsers(RentalOffersListDTO rentalOffersListDTO);
}
