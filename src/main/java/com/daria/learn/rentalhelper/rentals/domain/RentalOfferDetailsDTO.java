package com.daria.learn.rentalhelper.rentals.domain;

import com.daria.learn.rentalhelper.bot.domain.OutboundRentalOfferDTO;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;

public class RentalOfferDetailsDTO implements Serializable {

    private static final long serialVersionUID = 4532423567270838391L;

    @Getter
    private final String name;
    @Getter
    private final String link;
    @Getter
    @Nullable
    private final RentalStatus status; //TODO: enum?
    @Getter
    private final String postalCode;
    @Getter
    private final Double price;
    @Getter
    @Nullable
    private final Boolean includingServices;
    @Getter
    @Nullable
    private final Instant availableFrom;
    @Getter
    private final Boolean isFurnished;
    @Getter
    private final int area;
    @Getter @Nullable
    private final String agency;

    public RentalOfferDetailsDTO(String name, String link, RentalStatus status, String postalCode,
                                 Double price, Boolean includingServices, Instant availableFrom,
                                 Boolean isFurnished, int area, String agency) {
        this.name = name;
        this.link = link;
        this.postalCode = postalCode;
        this.price = price;
        this.includingServices = includingServices;
        this.availableFrom = availableFrom;
        this.status = status;
        this.isFurnished = isFurnished;
        this.area = area;
        this.agency = agency;
    }

    public static RentalOfferDetailsDTO missed(String link) {
        return new RentalOfferDetailsDTO(null, link, RentalStatus.DELETED, null, null, null, null, false, 0, null);
    }

    public boolean isDeleted() {
        return status == RentalStatus.DELETED;
    }

    public OutboundRentalOfferDTO toOutboundRentalOfferDTO() {
        return new OutboundRentalOfferDTO(name, price, link, area, agency, postalCode, isFurnished);
    }

    @Override
    public String toString() {
        return "RentalOfferDetailsDTO{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", status=" + status +
                ", postalCode='" + postalCode + '\'' +
                ", price=" + price +
                ", includingServices=" + includingServices +
                ", availableFrom=" + availableFrom +
                ", isFurnished=" + isFurnished +
                ", area=" + area +
                ", agency='" + agency + '\'' +
                '}';
    }
}
