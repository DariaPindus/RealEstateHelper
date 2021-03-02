package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;

import java.util.List;

public interface RentalPersistenceFacade {
    List<BriefRentalOfferDTO> persistNewRentals(List<BriefRentalOfferDTO> rentalOfferDTOS);
    List<String> getSourceOpenOffersUrls(String source);
    List<RentalOfferDetailsDTO> updateRentalDetails(List<RentalOfferDetailsDTO> allRentals);
}
