package com.daria.learn.rentalhelper.userservice.user.mapper;

import com.daria.learn.rentalhelper.dtos.UserDTO;
import com.daria.learn.rentalhelper.dtos.UserUpdateDTO;
import com.daria.learn.rentalhelper.userservice.common.mapper.DTOMapper;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.user.domain.User;
import com.daria.learn.rentalhelper.userservice.userpreference.mapper.UserPreferenceMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DTOMapper<UserDTO, User> {

    private final UserPreferenceMapper userPreferenceMapper;

    public UserMapper(UserPreferenceMapper userPreferenceMapper) {
        this.userPreferenceMapper = userPreferenceMapper;
    }

    @Override
    public User fromDTO(UserDTO userDTO) {
        SourceType sourceType = SourceType.valueOf(userDTO.getSourceType().name());

        User user = new User();
        user.setExternalId(userDTO.getUserId());
        user.setSourceType(sourceType);

        if (userDTO.getUserPreferenceDTO() != null) {
            user.setUserPreference(userPreferenceMapper.fromDTO(userDTO.getUserPreferenceDTO()));
        }

        return user;
    }

    @Override
    public UserDTO toDTO(User user) {
        throw new IllegalStateException("Not implemented");
    }

    public UserUpdateDTO toUpdateDTO(User user) {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUserId(user.getExternalId());
        userUpdateDTO.setUserPreferenceDTO(userPreferenceMapper.toDTO(user.getUserPreference()));
        return userUpdateDTO;
    }

}
