package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
public class BriefRentalOffersListDTO implements Serializable {
    private static final long serialVersionUID = 4091195088787402018L;

    @Getter @Setter
    private List<BriefRentalOfferDTO> rentalOffers;

    public BriefRentalOffersListDTO(List<BriefRentalOfferDTO> rentalOffers) {
        this.rentalOffers = rentalOffers;
    }
}
