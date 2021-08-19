package com.daria.learn.rentalhelper.core.persist;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.FetchDetailRequestDTO;

import java.util.List;

public interface RentalOfferFacade {
    List<BriefRentalOfferDTO> persistNewRentals(List<BriefRentalOfferDTO> rentalOfferDTOS);
    List<DetailRentalOffersDTO> updateRentalDetails(List<DetailRentalOffersDTO> allRentals);
    List<FetchDetailRequestDTO> getFetchDetailRequests();
}
