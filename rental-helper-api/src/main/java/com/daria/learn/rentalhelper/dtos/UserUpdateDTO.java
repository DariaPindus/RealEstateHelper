package com.daria.learn.rentalhelper.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = -4274192574450477584L;

    @Getter @Setter
    private String userId;

    @Getter @Setter
    private UserPreferenceDTO userPreferenceDTO;

}
