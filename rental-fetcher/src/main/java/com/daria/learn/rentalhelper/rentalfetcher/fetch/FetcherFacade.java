package com.daria.learn.rentalhelper.rentalfetcher.fetch;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;

import java.util.List;
import java.util.Set;

public interface FetcherFacade {
    List<BriefRentalOfferDTO> fetchOffers();
    RentalOfferDetailsDTO fetchOfferDetailFromSource(String source, String url);
    Set<String> getDataSourcesNames();
}
