package com.daria.learn.rentalhelper.core.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class FieldChange extends BaseEntity<Integer> {

    public static String NULL_VALUE = "null";

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

    public FieldChange() {
        this.time = Instant.now();
    }

    public FieldChange(String fieldName, String newValue, String oldValue, RentalOffer offer) {
        this.time = Instant.now();
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.offer = offer;
    }

    public FieldChange(String fieldName, String oldValue, String newValue) {
        this();
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
