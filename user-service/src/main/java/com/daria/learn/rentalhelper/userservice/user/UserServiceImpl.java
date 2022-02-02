package com.daria.learn.rentalhelper.userservice.user;

import com.daria.learn.rentalhelper.dtos.UserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.dtos.UserUpdateDTO;
import com.daria.learn.rentalhelper.userservice.userpreference.domain.UserPreference;
import com.daria.learn.rentalhelper.userservice.user.domain.projection.UserPreferenceUserProjection;
import com.daria.learn.rentalhelper.userservice.user.exception.UserNotFoundException;
import com.daria.learn.rentalhelper.userservice.user.mapper.UserMapper;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.user.domain.User;
import com.daria.learn.rentalhelper.userservice.userpreference.mapper.UserPreferenceMapper;
import com.daria.learn.rentalhelper.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPreferenceMapper userPreferenceMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           UserPreferenceMapper userPreferenceMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userPreferenceMapper = userPreferenceMapper;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        User user = userMapper.fromDTO(userDTO);
        userRepository.save(user);
        return userDTO;
    }

    @Override
    public UserPreferenceDTO getUserPreferenceBySourceTypeAndId(SourceType sourceType, String userId) {
        Optional<UserPreferenceUserProjection> userPreferenceUserProjectionOptional =
                userRepository.findUserPreferenceBySourceTypeAndExternalId(sourceType, userId);
        if (userPreferenceUserProjectionOptional.isEmpty())
            throw new UserNotFoundException(userId, sourceType);

        UserPreference userPreference = userPreferenceUserProjectionOptional.get().getUserPreference();
        return userPreferenceMapper.toDTO(userPreference);
    }

    @Override
    public UserUpdateDTO updateUser(SourceType sourceType, UserUpdateDTO userDTO) {
        User user = userRepository.findBySourceTypeAndExternalId(sourceType, userDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(userDTO.getUserId(), sourceType));
        user.setUserPreference(userPreferenceMapper.fromDTO(userDTO.getUserPreferenceDTO()));

        userRepository.save(user);

        return userMapper.toUpdateDTO(user);
    }
}
