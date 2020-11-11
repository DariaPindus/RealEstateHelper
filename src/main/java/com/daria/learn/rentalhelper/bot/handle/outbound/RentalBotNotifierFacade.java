package com.daria.learn.rentalhelper.bot.handle.outbound;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;

public interface RentalBotNotifierFacade {
    void notifySubscribedUsers(RentalOffersListDTO rentalOffersListDTO);
}
