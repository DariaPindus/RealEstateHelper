package com.daria.learn.rentalhelper.userservice.user.controller;

import com.daria.learn.rentalhelper.dtos.UserDTO;
import com.daria.learn.rentalhelper.dtos.UserPreferenceDTO;
import com.daria.learn.rentalhelper.dtos.UserUpdateDTO;
import com.daria.learn.rentalhelper.userservice.user.UserService;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public UserDTO addUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @PutMapping("/user/{sourcetype}")
    public UserUpdateDTO updateUser(@PathVariable("sourcetype") String sourceType,
                              @RequestBody UserUpdateDTO userDTO) {
        return userService.updateUser(SourceType.valueOf(sourceType), userDTO);
    }

    @GetMapping("/user/{sourcetype}/{userid}/userpreference")
    public UserPreferenceDTO getUserPreference(@PathVariable("sourcetype") String sourceType,
                                               @PathVariable("userid") String userId) {
        return userService.getUserPreferenceBySourceTypeAndId(SourceType.valueOf(sourceType), userId);
    }

}
