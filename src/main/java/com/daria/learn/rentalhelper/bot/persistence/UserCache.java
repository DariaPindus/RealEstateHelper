package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserBotInfo;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserCache {
    Optional<BotStateEnum> getUserState(Integer userId);

    Optional<UserPreference> getUserPreference(Integer userId);

    void setUserPreferenceFromMessage(Message message, @Nullable UserPreference userPreference);

    void setUserStateFromMessage(Message message, BotStateEnum stateEnum);

    List<UserBotInfo> getSubscribedUserInfos();
}
