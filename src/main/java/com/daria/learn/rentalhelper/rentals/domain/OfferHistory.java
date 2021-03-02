package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class OfferHistory extends BaseEntity<Integer> {

    @Getter @Setter
    private Instant time;
    @Getter @Setter
    private String fieldName;
    @Getter @Setter
    private String newValue;
    @Getter @Setter
    private String oldValue;
    @ManyToOne
    @Getter @Setter
    private RentalOffer offer;

    public OfferHistory() {
    }

    public OfferHistory(Instant time) {
        this.time = time;
    }

    public OfferHistory(Instant time, String fieldName, String newValue, String oldValue, RentalOffer offer) {
        this.time = time;
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.offer = offer;
    }
}
