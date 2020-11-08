package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
public class RentalOffersListDTO implements Serializable {
    private static final long serialVersionUID = 6308857201913078333L;

    @Getter
    private List<RentalOfferDTO> rentalOfferDTOS;

    public RentalOffersListDTO(List<RentalOfferDTO> rentalOfferDTOS) {
        this.rentalOfferDTOS = rentalOfferDTOS;
    }
}
