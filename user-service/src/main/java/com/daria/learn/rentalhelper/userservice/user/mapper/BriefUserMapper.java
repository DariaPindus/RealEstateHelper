package com.daria.learn.rentalhelper.userservice.user.mapper;

import com.daria.learn.rentalhelper.dtos.TelegramUserDTO;
import com.daria.learn.rentalhelper.userservice.common.mapper.DTOMapper;
import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class BriefUserMapper implements DTOMapper<TelegramUserDTO, User> {

    @Override
    public User fromDTO(TelegramUserDTO telegramUserDTO) {
        throw new IllegalStateException();
    }

    @Override
    public TelegramUserDTO toDTO(User user) {
        if (user.getSourceType() != SourceType.Telegram)
            throw new IllegalStateException("Cannot transform to TelegramUserDTO, user has different source type " + user.getSourceType());

        return new TelegramUserDTO(user.getExternalId());
    }

}
