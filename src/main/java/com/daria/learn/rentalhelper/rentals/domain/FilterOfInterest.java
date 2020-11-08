package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;

import java.util.Set;

public class FilterOfInterest {
    @Getter
    private final double priceUp;
    @Getter
    private final Set<String> postalCodes;
    @Getter
    private final int minArea;
    @Getter
    private final boolean furnished;

    public FilterOfInterest(double priceUp, Set<String> postalCodes, int minArea, boolean furnished) {
        this.priceUp = priceUp;
        this.postalCodes = postalCodes;
        this.minArea = minArea;
        this.furnished = furnished;
    }
}
