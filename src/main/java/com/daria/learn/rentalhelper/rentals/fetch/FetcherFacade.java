package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;
import java.util.Set;

public interface FetcherFacade {
    List<BriefRentalOfferDTO> fetchOffers();
    RentalOfferDetailsDTO fetchOfferDetailFromSource(String source, String url);
    Set<String> getDataSourcesNames();
}
