package com.daria.learn.rentalhelper.bot.handle;

import com.daria.learn.rentalhelper.bot.model.UserPreference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
public class UserBotInfo {
    private final Integer id;
    @Getter @Setter
    private BotStateEnum state;
    @Getter @Setter
    @Nullable
    private UserPreference userPreference;

    public UserBotInfo(Integer id) {
        this.id = id;
    }

}
