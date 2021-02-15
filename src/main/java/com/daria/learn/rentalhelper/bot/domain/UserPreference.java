package com.daria.learn.rentalhelper.bot.domain;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Set;

public class UserPreference {
    @Getter @Nullable
    private final Double maxPrice;
    @Getter @Nullable
    private final Set<String> postalCodes;
    @Getter @Nullable
    private final Integer minArea;
    @Getter @Nullable
    private final Boolean furnished;

    public UserPreference(Double maxPrice, Set<String> postalCodes, Integer minArea, Boolean furnished) {
        this.maxPrice = maxPrice;
        this.postalCodes = postalCodes;
        this.minArea = minArea;
        this.furnished = furnished;
    }

    public boolean isMatching(OutboundRentalOfferDTO rentalOfferDTO) {
        boolean result = true;

        if (maxPrice != null)
            result &= rentalOfferDTO.getPrice() <= maxPrice;
        if (minArea != null)
            result &= rentalOfferDTO.getArea() >= minArea;
        if (furnished != null)
            result &= rentalOfferDTO.isFurnished() == furnished;

        return result;
    }
}
