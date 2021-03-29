package com.daria.learn.rentalhelper.rentalfetcher;


import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalStatus;

import java.time.Instant;

import static com.daria.learn.rentalhelper.rentalfetcher.Random.*;

public class DomainHelper {

    public static RentalOfferDetailsDTO fromBriefRentalOfferDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return fromBriefRentalOfferDTOUpdated(briefRentalOfferDTO, briefRentalOfferDTO.getPrice(), Instant.now());
    }

    public static RentalOfferDetailsDTO fromBriefRentalOfferDTOUpdated(BriefRentalOfferDTO briefRentalOfferDTO, double newPrice, Instant newAvailable) {
        return new RentalOfferDetailsDTO(briefRentalOfferDTO.getName(), briefRentalOfferDTO.getLink(), RentalStatusDTO.AVAILABLE, briefRentalOfferDTO.getPostalCode(),
                newPrice, null, newAvailable, null, briefRentalOfferDTO.getArea(), briefRentalOfferDTO.getAgency());
    }

    public static RentalOfferDetailsDTO missedFromBriefRentalOfferDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return RentalOfferDetailsDTO.missed(briefRentalOfferDTO.getLink());
    }

    public static BriefRentalOfferDTO createBriefRentalOfferDTO(String name, String source) {
        return new BriefRentalOfferDTO(name, getRandomPostalCode(), getRandomNumber(20, 200), getRandomString(20),
                "/"+ Instant.now().getNano(), getRandomNumber(100, 2000), source);
    }

    public static BriefRentalOfferDTO copyBriefRentalOfferDTO(BriefRentalOfferDTO origin, String name) {
        return new BriefRentalOfferDTO(name, origin.getPostalCode(), origin.getArea(), origin.getAgency(), origin.getLink(), origin.getPrice(), origin.getSource());
    }

    public static RentalOfferDetailsDTO copyRentalOfferDetailsDTOUpdated(RentalOfferDetailsDTO origin, Boolean isFurnished,
                                                                         String name, RentalStatusDTO rentalStatus,
                                                                         Instant availableFrom, Double price) {
        return new RentalOfferDetailsDTO(name, origin.getLink(), rentalStatus, origin.getPostalCode(), price,
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
