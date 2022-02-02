package com.daria.learn.rentalhelper.userservice.userpreference.mapper;

import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.userservice.common.mapper.DTOMapper;
import com.daria.learn.rentalhelper.userservice.userpreference.domain.UserPreference;
import org.springframework.stereotype.Component;

@Component
public class UserPreferenceMapper implements DTOMapper<UserPreferenceDTO, UserPreference> {

    @Override
    public UserPreference fromDTO(UserPreferenceDTO userPreferenceDTO) {
        return new UserPreference(userPreferenceDTO.getMaxPrice(),
                userPreferenceDTO.getPostalCodes(),
                userPreferenceDTO.getMinArea(),
                userPreferenceDTO.getFurnished());
    }

    @Override
    public UserPreferenceDTO toDTO(UserPreference userPreference) {
        UserPreferenceDTO userPreferenceDTO = new UserPreferenceDTO();
        userPreferenceDTO.setFurnished(userPreference.getFurnished());
        userPreferenceDTO.setMaxPrice(userPreference.getMaxPrice());
        userPreferenceDTO.setMinArea(userPreference.getMinArea());
        userPreferenceDTO.setPostalCodes(userPreference.getPostalCodes());
        return userPreferenceDTO;
    }

}
