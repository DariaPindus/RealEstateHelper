package com.daria.learn.rentalhelper.bot.model;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Locale;

@AllArgsConstructor
public class UserBotInfo {
    @Getter
    private final Long chatId;
    @Getter @Setter
    private BotStateEnum state;
    @Getter @Setter
    @Nullable
    private UserPreference userPreference;
    @Getter @Setter
    private Locale userLocale;

    public UserBotInfo(Long chatId, BotStateEnum state, UserPreference userPreference) {
        this.chatId = chatId;
        this.state = state;
        this.userPreference = userPreference;
    }

}
