package com.daria.learn.rentalhelper.userservice.userpreference;

import com.daria.learn.rentalhelper.dtos.BriefUserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.user.mapper.BriefUserMapper;
import com.daria.learn.rentalhelper.userservice.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserRepository userRepository;
    private final BriefUserMapper briefUserMapper;

    public UserPreferenceServiceImpl(UserRepository userRepository, BriefUserMapper briefUserMapper) {
        this.userRepository = userRepository;
        this.briefUserMapper = briefUserMapper;
    }

    @Override
    public List<BriefUserDTO> findMatchingUsers(SourceType sourceType, UserPreferenceDTO userPreferenceDTO) {
        return userRepository.findAllByMatchingPreferenceAndSourceType(sourceType,
                                                                        userPreferenceDTO.getMaxPrice(),
                                                                        userPreferenceDTO.getMinArea(),
                                                                        userPreferenceDTO.getFurnished())
                .stream().map(briefUserMapper::toDTO)
                .collect(Collectors.toUnmodifiableList());
    }

}
