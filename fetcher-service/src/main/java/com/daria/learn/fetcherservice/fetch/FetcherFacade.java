package com.daria.learn.fetcherservice.fetch;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;

import java.util.List;

public interface FetcherFacade {
    List<BriefRentalOfferDTO> fetchOffersFromSource(String source);
    List<BriefRentalOfferDTO> fetchOffers();
    List<DetailRentalOffersDTO> fetchOfferDetailsFromSource(String source, List<String> url);
}
