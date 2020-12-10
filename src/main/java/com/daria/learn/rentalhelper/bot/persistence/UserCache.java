package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import com.daria.learn.rentalhelper.bot.domain.UserBotInfo;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @param <T> is type of chat identifier
 */
public interface UserCache<T> {
    Optional<BotStateEnum> getUserState(T userChatId);

    Optional<UserPreference> getUserPreference(T userChatId);

    void setUserPreference(T userChatId, @Nullable UserPreference userPreference);

    void setUserState(T userChatId, BotStateEnum stateEnum);

    List<UserBotInfo> getSubscribedUserInfos();

    Locale getUserLocale(T userChatId);

    void setUserLocale(T userChatId, Locale userLocale);
}
