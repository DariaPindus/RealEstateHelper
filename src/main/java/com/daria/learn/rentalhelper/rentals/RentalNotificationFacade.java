package com.daria.learn.rentalhelper.rentals;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;

import java.util.List;

public interface RentalNotificationFacade {
    void saveAndNotifyNewRentals(List<RentalOfferDTO> offerDTOList);
}
