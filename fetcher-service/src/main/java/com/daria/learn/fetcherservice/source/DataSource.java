package com.daria.learn.fetcherservice.source;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;

import java.util.List;

public interface DataSource {
    String getSourceName();
    List<BriefRentalOfferDTO> getNewOffers();
    DetailRentalOffersDTO fetchOfferDetail(String url);
}
