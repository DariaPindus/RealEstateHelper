package com.daria.learn.rentalhelper.rentalfetcher.sources;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;

import java.util.List;

public interface DataSource {
    String getSourceName();
    List<BriefRentalOfferDTO> getOffers();
    RentalOfferDetailsDTO fetchOfferDetail(String url);
}
