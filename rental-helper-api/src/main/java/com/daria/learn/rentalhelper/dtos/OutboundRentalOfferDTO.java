package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;

import java.io.Serializable;

public class OutboundRentalOfferDTO implements Serializable {

    private static final long serialVersionUID = -7875692315562148863L;

    @Getter
    private final String name;
    @Getter
    private final double price;
    @Getter
    private final String link;
    @Getter
    private final int area;
    @Getter
    private final String agency;
    @Getter
    private final String postalCode;
    @Getter
    private final boolean isFurnished;

    public OutboundRentalOfferDTO(String name, double price, String link, int area, String agency, String postalCode, boolean isFurnished) {
        this.name = name;
        this.price = price;
        this.link = link;
        this.area = area;
        this.agency = agency;
        this.postalCode = postalCode;
        this.isFurnished = isFurnished;
    }
}
