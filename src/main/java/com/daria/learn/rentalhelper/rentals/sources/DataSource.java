package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;

import java.util.List;

public interface DataSource {
    List<RentalOfferDTO> getOffers();
}
