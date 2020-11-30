package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserBotInfo;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
