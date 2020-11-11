package com.daria.learn.rentalhelper.bot.model;

import lombok.Getter;

import java.util.Set;

public class UserPreference {
    @Getter
    private final double priceUp;
    @Getter
    private final Set<String> postalCodes;
    @Getter
    private final int minArea;
    @Getter
    private final boolean furnished;

    public UserPreference(double maxPrice, Set<String> postalCodes, int minArea, boolean furnished) {
        this.priceUp = maxPrice;
        this.postalCodes = postalCodes;
        this.minArea = minArea;
        this.furnished = furnished;
    }
}
