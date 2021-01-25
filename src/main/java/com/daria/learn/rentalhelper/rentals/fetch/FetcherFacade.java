package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;
import java.util.Optional;

public interface FetcherFacade {
    List<RentalOfferDTO> fetchOffers();
    List<RentalOfferDetailsDTO> fetchOfferDetails(List<String> url);
    Optional<RentalOfferDetailsDTO> fetchOfferDetail(String url);
}
