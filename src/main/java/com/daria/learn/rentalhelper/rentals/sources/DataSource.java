package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;
import java.util.Optional;

public interface DataSource {
    List<RentalOfferDTO> getOffers();
    List<RentalOfferDetailsDTO> getOfferDetails(List<String> urls);
    Optional<RentalOfferDetailsDTO> fetchOfferDetail(String url);
}
