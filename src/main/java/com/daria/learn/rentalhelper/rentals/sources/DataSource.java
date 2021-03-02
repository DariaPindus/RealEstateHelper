package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;

public interface DataSource {
    String getSourceName();
    List<BriefRentalOfferDTO> getOffers();
    RentalOfferDetailsDTO fetchOfferDetail(String url);
}
