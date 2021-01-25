package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;

import java.util.List;

public interface RentalPersistenceFacade {
    List<RentalOfferDTO> persistNewRentals(List<RentalOfferDTO> rentalOfferDTOS);
    List<String> getOpenOffersUrls();
}
