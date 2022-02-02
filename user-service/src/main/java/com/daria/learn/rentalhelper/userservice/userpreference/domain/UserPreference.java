package com.daria.learn.rentalhelper.userservice.userpreference.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import java.util.Set;

@Getter
@Setter
@ToString
public class UserPreference {
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

}
