package com.daria.learn.rentalhelper.core.domain;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.LinkedList;

@Entity
@DiscriminatorValue(ParariusRentalOffer.DISCRIMINATOR_VALUE)
public class ParariusRentalOffer extends RentalOffer {

    public final static String DISCRIMINATOR_VALUE = "pararius";

    public ParariusRentalOffer() {}

    public ParariusRentalOffer(String name, String postalCode, int area, double price, String agency, String link) {
        setName(name);
        setPostalCode(postalCode);
        setArea(area);
        setPrice(price);
        setAgency(agency);
        setLink(link);
    }

    public static ParariusRentalOffer fromBriefRentalOfferDTO(BriefRentalOfferDTO offerDTO) {
        return new ParariusRentalOffer(offerDTO.getName(), offerDTO.getPostalCode(), offerDTO.getArea(), offerDTO.getPrice(), offerDTO.getAgency(), offerDTO.getLink());
    }
}
