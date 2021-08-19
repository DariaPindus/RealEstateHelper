package com.daria.learn.rentalhelper.bot.notification;


import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;

import java.util.List;

public interface RentalBotNotifierFacade {
    void notifySubscribedUsers(List<DetailRentalOffersDTO> rentalOffersListDTO);
}
