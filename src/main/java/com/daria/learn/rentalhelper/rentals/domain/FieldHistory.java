package com.daria.learn.rentalhelper.rentals.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FieldHistory {
    @Getter @Setter
    private String fieldName;
    @Getter @Setter
    private String fieldValue;
    @Getter @Setter
    private String oldValue;
}
