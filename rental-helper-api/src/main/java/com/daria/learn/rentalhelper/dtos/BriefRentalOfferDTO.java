package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class BriefRentalOfferDTO implements Serializable {
    private static final long serialVersionUID = 6825785700959279609L;
    @Getter
    private final String name;
    @Getter
    private final String postalCode;
    @Getter
    private final Integer area;
    @Getter
    private final String agency;
    @Getter
    private final String link;
    @Getter
    private final Double price;
    @Getter @Setter
    private String source;

    public BriefRentalOfferDTO(String name, String postalCode, int area, String agency, String link, double price, String source) {
        this.name = name;
        this.postalCode = postalCode;
        this.area = area;
        this.agency = agency;
        this.link = link;
        this.price = price;
        this.source = source;
    }

    @Override
    public int hashCode() {
        return 11 * this.name.hashCode() + this.postalCode.hashCode() + this.agency.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BriefRentalOfferDTO))
            return false;
        BriefRentalOfferDTO that = (BriefRentalOfferDTO)obj;
        return that.postalCode.equals(this.postalCode)
                && that.name.equals(this.name)
                && that.agency.equals(this.agency);
    }

    @Override
    public String toString() {
        return "RentalOfferDTO{" +
                "name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", area=" + area +
                ", agency='" + agency + '\'' +
                ", link='" + link + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
