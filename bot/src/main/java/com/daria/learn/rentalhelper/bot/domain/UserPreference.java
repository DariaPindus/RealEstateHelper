package com.daria.learn.rentalhelper.bot.domain;

import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Set;

public class UserPreference implements Serializable {
    private static final long serialVersionUID = -2886920347632094707L;
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

    public boolean isMatching(DetailRentalOffersDTO rentalOfferDTO) {
        boolean result = true;

        if (maxPrice != null)
            result &= rentalOfferDTO.getPrice() <= maxPrice;
        if (minArea != null)
            result &= rentalOfferDTO.getArea() >= minArea;
        if (furnished != null)
            result &= rentalOfferDTO.getIsFurnished() == furnished;

        return result;
    }
}
