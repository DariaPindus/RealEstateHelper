package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;

import java.util.List;

public interface FetcherFacade {
    List<RentalOfferDTO> fetchOffers();
}
