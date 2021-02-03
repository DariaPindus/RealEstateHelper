package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class FieldHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    private Integer id;
    @Getter @Setter
    private Instant time;
    @Getter @Setter
    @ManyToOne
    private RentalOffer rentalOffer;
    @Getter @Setter
    private String fieldName;
    @ManyToOne
    private BaseEntity parent;

    public FieldHistory() {
    }

    public FieldHistory(Instant time) {
        this.time = time;
    }

    public FieldHistory(RentalOffer rentalOffer, String fieldName) {
        this.rentalOffer = rentalOffer;
        this.fieldName = fieldName;
    }

}
