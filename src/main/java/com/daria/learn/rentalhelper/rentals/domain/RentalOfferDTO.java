package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;

import java.io.Serializable;

public class RentalOfferDTO implements Serializable {
    private static final long serialVersionUID = 6825785700959279609L;
    @Getter
    private final String name;
    @Getter
    private final String postalCode;
    @Getter
    private final double price;
    @Getter
    private final int area;
    @Getter
    private final String agency;
    @Getter
    private final boolean furnished;
    @Getter
    private final String link;

    public RentalOfferDTO(String name, String postalCode, double price, int area, String agency, boolean furnished, String link) {
        this.name = name;
        this.postalCode = postalCode;
        this.price = price;
        this.area = area;
        this.agency = agency;
        this.furnished = furnished;
        this.link = link;
    }

    @Override
    public int hashCode() {
        return 11 * this.name.hashCode() + this.postalCode.hashCode() + this.agency.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RentalOfferDTO))
            return false;
        RentalOfferDTO that = (RentalOfferDTO)obj;
        return that.postalCode.equals(this.postalCode)
                && that.name.equals(this.name)
                && that.price == this.price
                && that.agency.equals(this.agency);
    }

    @Override
    public String toString() {
        return "RentalOfferDTO{" +
                "name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", price=" + price +
                ", area=" + area +
                ", agency='" + agency + '\'' +
                ", furnished=" + furnished +
                ", link='" + link + '\'' +
                '}';
    }
}
