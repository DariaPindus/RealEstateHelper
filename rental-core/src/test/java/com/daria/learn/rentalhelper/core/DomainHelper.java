package com.daria.learn.rentalhelper.core;


import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import com.daria.learn.rentalhelper.core.domain.RentalOffer;

import java.time.Instant;

import static com.daria.learn.rentalhelper.core.Random.*;

public class DomainHelper {

    public static DetailRentalOffersDTO fromBriefRentalOfferDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return fromBriefRentalOfferDTOUpdated(briefRentalOfferDTO, briefRentalOfferDTO.getPrice(), Instant.now());
    }

    public static DetailRentalOffersDTO fromBriefRentalOfferDTOUpdated(BriefRentalOfferDTO briefRentalOfferDTO, double newPrice, Instant newAvailable) {
        return new DetailRentalOffersDTO(briefRentalOfferDTO.getName(), briefRentalOfferDTO.getLink(), RentalStatusDTO.AVAILABLE, briefRentalOfferDTO.getPostalCode(),
                newPrice, null, newAvailable, null, briefRentalOfferDTO.getArea(), briefRentalOfferDTO.getAgency());
    }

    public static DetailRentalOffersDTO missedFromBriefRentalOfferDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return DetailRentalOffersDTO.missed(briefRentalOfferDTO.getLink());
    }

    public static BriefRentalOfferDTO createBriefRentalOfferDTO(String name, String source) {
        return new BriefRentalOfferDTO(name, getRandomPostalCode(), getRandomNumber(20, 200), getRandomString(20),
                "/"+ Instant.now().getNano(), getRandomNumber(100, 2000), source);
    }

    public static BriefRentalOfferDTO copyBriefRentalOfferDTO(BriefRentalOfferDTO origin, String name) {
        return new BriefRentalOfferDTO(name, origin.getPostalCode(), origin.getArea(), origin.getAgency(), origin.getLink(), origin.getPrice(), origin.getSource());
    }

    public static DetailRentalOffersDTO copyRentalOfferDetailsDTOUpdated(DetailRentalOffersDTO origin, Boolean isFurnished,
                                                                         String name, RentalStatusDTO rentalStatus,
                                                                         Instant availableFrom, Double price) {
        return new DetailRentalOffersDTO(name, origin.getLink(), rentalStatus, origin.getPostalCode(), price,
                origin.getIncludingServices(), availableFrom, isFurnished, origin.getArea(), origin.getAgency());
    }

    public static boolean isSameRentalOffer(BriefRentalOfferDTO rentalOfferDTO, RentalOffer rentalOffer) {
        return rentalOfferDTO.getName().equals(rentalOffer.getName()) &&
                rentalOfferDTO.getSource().equals(rentalOffer.getSource()) &&
                rentalOfferDTO.getLink().equals(rentalOffer.getLink()) &&
                rentalOffer.getPrice() == rentalOffer.getPrice() &&
                rentalOfferDTO.getPostalCode().equals(rentalOffer.getPostalCode()) &&
                rentalOfferDTO.getArea() == rentalOffer.getArea() &&
                rentalOfferDTO.getAgency().equals(rentalOffer.getAgency());
    }
}
