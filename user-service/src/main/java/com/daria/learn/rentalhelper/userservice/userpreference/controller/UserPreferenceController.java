package com.daria.learn.rentalhelper.userservice.userpreference.controller;

import com.daria.learn.rentalhelper.dtos.BriefUserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.userpreference.UserPreferenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/userpreference")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @GetMapping(value = "/{sourceType}", params = {"maxPrice", "minArea", "furnished"})
    public List<BriefUserDTO> findAllMatchingUsers(@PathVariable SourceType sourceType,
                                                   Double maxPrice, Integer minArea, Boolean furnished) {
        return userPreferenceService.findMatchingUsers(sourceType, new UserPreferenceDTO(maxPrice, minArea, furnished));
    }

}
