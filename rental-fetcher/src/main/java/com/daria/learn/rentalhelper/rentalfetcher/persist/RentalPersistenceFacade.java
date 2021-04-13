package com.daria.learn.rentalhelper.rentalfetcher.persist;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;

import java.time.Instant;
import java.util.List;

public interface RentalPersistenceFacade {
    List<BriefRentalOfferDTO> persistNewRentals(List<BriefRentalOfferDTO> rentalOfferDTOS);
    List<String> getSourceOpenOffersUrls(String source);
    List<RentalOfferDetailsDTO> updateRentalDetails(List<RentalOfferDetailsDTO> allRentals);
    List<BriefRentalOfferDTO> getAllAfter(Instant time);
}
