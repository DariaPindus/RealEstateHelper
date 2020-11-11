package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserPreference;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public interface UserCache {
    Optional<BotStateEnum> getUserState(Integer userId);

    Optional<UserPreference> getUserPreference(Integer userId);

    void setUserPreference(Integer userId, UserPreference userPreference);

    void setUserState(Integer userId, BotStateEnum stateEnum);

    Set<Integer> getSubscribedUserIds();
}
