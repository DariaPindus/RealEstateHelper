package com.daria.learn.rentalhelper.userservice.user.exception;

import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 8384535951514426884L;

    private static final String exceptionFormat = "No user %s found for sourcetype %s";

    public UserNotFoundException(String userId, SourceType sourceType) {
        super(String.format(exceptionFormat, userId, sourceType));
    }

}
