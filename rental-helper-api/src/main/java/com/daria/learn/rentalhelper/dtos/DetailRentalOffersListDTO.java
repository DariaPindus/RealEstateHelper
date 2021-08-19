package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
public class DetailRentalOffersListDTO implements Serializable {

    private static final long serialVersionUID = 4228460633883420048L;

    @Getter @Setter
    private List<DetailRentalOffersDTO> offersList;

    public DetailRentalOffersListDTO(List<DetailRentalOffersDTO> offersList) {
        this.offersList = offersList;
    }
}
