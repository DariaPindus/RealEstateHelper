package com.daria.learn.rentalhelper.rentals.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;

@Embeddable
@NoArgsConstructor
public class FieldHistory {
    @Getter @Setter
    private String fieldName;
    @Getter @Setter
    private String fieldValue;
    @Getter @Setter
    private String oldValue;
    @Getter @Setter
    private Double delta;

    public FieldHistory(String fieldName, String fieldValue, String oldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.oldValue = oldValue;
    }

    @PrePersist
    void calculateDelta() {
        try {
            if (fieldName.equals("price")) {
                delta = Double.parseDouble(fieldValue) - Double.parseDouble(oldValue);
            }
        } catch (RuntimeException ex) {
            //do nothing
        }
    }
}
