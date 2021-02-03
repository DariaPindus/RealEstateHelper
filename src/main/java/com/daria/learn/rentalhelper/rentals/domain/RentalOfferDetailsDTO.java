package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import org.springframework.lang.Nullable;

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
    private final Double price;
    @Getter
    @Nullable
    private final Boolean includingServices;
    @Getter
    @Nullable
    private final Instant availableFrom;
    @Getter
    @Nullable
    private final Boolean isFurnished;
    @Getter
    private final double area;

    public RentalOfferDetailsDTO(String name, String link, RentalStatus status, Double price, Boolean includingServices, Instant availableFrom, Boolean isFurnished, double area) {
        this.name = name;
        this.link = link;
        this.price = price;
        this.includingServices = includingServices;
        this.availableFrom = availableFrom;
        this.status = status;
        this.isFurnished = isFurnished;
        this.area = area;
    }

    public static RentalOfferDetailsDTO missed(String link) {
        return new RentalOfferDetailsDTO(null, link, RentalStatus.DELETED, null, null, null, null, 0);
    }

    public boolean isDeleted() {
        return status == RentalStatus.DELETED;
    }

    @Override
    public String toString() {
        return "RentalOfferDetailsDTO{" +
                "price=" + price +
                ", availableFrom=" + availableFrom +
                ", status='" + status + '\'' +
                ", isFurnished=" + isFurnished +
                '}';
    }
}
