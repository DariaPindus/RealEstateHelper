package com.daria.learn.rentalhelper.bot.domain;

import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Locale;

@AllArgsConstructor
public class UserBotInfo implements Serializable {
    private static final long serialVersionUID = 4662180279480511941L;

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

    public UserBotInfo(Long chatId) {
        this.chatId = chatId;
    }
}
