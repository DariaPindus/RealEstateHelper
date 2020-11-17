package com.daria.learn.rentalhelper.bot.model;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
public class UserBotInfo {
    private final Integer userId;
    @Getter
    private final Long chatId;
    @Getter @Setter
    private BotStateEnum state;
    @Getter @Setter
    @Nullable
    private UserPreference userPreference;

    public UserBotInfo(Integer userId, Long chatId) {
        this.userId = userId;
        this.chatId = chatId;
    }

}
