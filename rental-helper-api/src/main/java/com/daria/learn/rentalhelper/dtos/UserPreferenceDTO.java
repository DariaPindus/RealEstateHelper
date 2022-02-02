package com.daria.learn.rentalhelper.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class UserPreferenceDTO implements Serializable {

    private static final long serialVersionUID = 8603534872919162719L;

    @Getter @Setter
    private Double maxPrice;
    @Getter @Setter
    private Set<String> postalCodes;
    @Getter @Setter
    private Integer minArea;
    @Getter @Setter
    private Boolean furnished;

    public UserPreferenceDTO(Double maxPrice, Set<String> postalCodes, Integer minArea, Boolean furnished) {
        this.maxPrice = maxPrice;
        this.postalCodes = postalCodes;
        this.minArea = minArea;
        this.furnished = furnished;
    }

    public UserPreferenceDTO(Double maxPrice, Integer minArea, Boolean furnished) {
        this(maxPrice, new HashSet<>(), minArea, furnished);
    }
}
