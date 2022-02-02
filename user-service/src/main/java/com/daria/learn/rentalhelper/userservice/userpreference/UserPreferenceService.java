package com.daria.learn.rentalhelper.userservice.userpreference;

import com.daria.learn.rentalhelper.dtos.BriefUserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;

import java.util.List;

public interface UserPreferenceService {

    List<BriefUserDTO> findMatchingUsers(SourceType sourceType, UserPreferenceDTO userPreferenceDTO);

}
