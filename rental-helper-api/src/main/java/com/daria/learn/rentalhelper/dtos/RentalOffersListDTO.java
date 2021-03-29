package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class RentalOffersListDTO implements Serializable {
    private static final long serialVersionUID = 6308857201913078333L;

    @Getter
    private List<OutboundRentalOfferDTO> rentalOfferDTOS;

    public RentalOffersListDTO(List<OutboundRentalOfferDTO> rentalOfferDTOS) {
        this.rentalOfferDTOS = rentalOfferDTOS;
    }

    public static RentalOffersListDTO fromDetailsDTO(List<RentalOfferDetailsDTO> detailsDTOS) {
        return new RentalOffersListDTO(detailsDTOS.stream().map(RentalOfferDetailsDTO::toOutboundRentalOfferDTO).collect(Collectors.toList()));
    }

    public int size() {
        return rentalOfferDTOS.size();
    }
}
