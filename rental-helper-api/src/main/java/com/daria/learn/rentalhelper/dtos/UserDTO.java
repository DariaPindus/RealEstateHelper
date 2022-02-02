package com.daria.learn.rentalhelper.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    private static final long serialVersionUID = -3631435970893496417L;

    @Getter @Setter
    private String userId;

    @Getter @Setter
    private UserPreferenceDTO userPreferenceDTO;

    @Getter @Setter
    private SourceType sourceType;
}
