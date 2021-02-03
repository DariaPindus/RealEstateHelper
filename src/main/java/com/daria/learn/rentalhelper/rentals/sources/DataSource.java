package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;

public interface DataSource {
    String getSourceName();
    List<RentalOfferDTO> getOffers();
    RentalOfferDetailsDTO fetchOfferDetail(String url);
}
