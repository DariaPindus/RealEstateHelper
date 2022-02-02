package com.daria.learn.rentalhelper.userservice.user;

import com.daria.learn.rentalhelper.dtos.UserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.dtos.UserUpdateDTO;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;

import java.util.Optional;

public interface UserService {

    UserDTO addUser(UserDTO userDTO);

    UserPreferenceDTO getUserPreferenceBySourceTypeAndId(SourceType sourceType, String userId);

    UserUpdateDTO updateUser(SourceType sourceType, UserUpdateDTO userDTO);
}
