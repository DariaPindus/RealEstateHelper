package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.exceptions.ExceededMaximumSubscriptionsAmountException;
import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.domain.UserBotInfo;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LocalUserCache implements UserCache<Long> {

    private static final int MAX_USERS_AMOUNT = 100;
    private final Map<Long, UserBotInfo> userBotMap = new HashMap<>();

    @Override
    public Optional<BotStateEnum> getUserState(Long userChatId) {
        return Optional.ofNullable(userBotMap.get(userChatId)).map(UserBotInfo::getState);
    }

    @Override
    public Optional<UserPreference> getUserPreference(Long userChatId) {
        return Optional.ofNullable(userBotMap.get(userChatId)).map(UserBotInfo::getUserPreference);
    }

    @Override
    public void setUserPreference(Long userChatId, UserPreference userPreference) {
        if (userBotMap.containsKey(userChatId)) {
            UserBotInfo userBotInfo = userBotMap.get(userChatId);
            userBotInfo.setState(BotStateEnum.SUBSCRIBED);
            userBotInfo.setUserPreference(userPreference);
        } else {
            UserBotInfo userBotInfo = new UserBotInfo(userChatId, BotStateEnum.SUBSCRIBED, userPreference);
            safePutToMap(userChatId, userBotInfo);
        }
    }

    @Override
    public void setUserState(Long userChatId, BotStateEnum stateEnum) {
        if (userBotMap.containsKey(userChatId)) {
            userBotMap.get(userChatId).setState(stateEnum);
        } else {
            safePutToMap(userChatId, new UserBotInfo(userChatId, stateEnum, null));
        }
    }

    @Override
    //TODO: optimize (cache ?)
    public List<UserBotInfo> getSubscribedUserInfos() {
        return userBotMap.values().stream()
                .filter(userBotInfo -> userBotInfo.getState() == BotStateEnum.SUBSCRIBED)
                .collect(Collectors.toList());
    }

    @Override
    public Locale getUserLocale(Long userChatId) {
        return Optional.ofNullable(userBotMap.get(userChatId)).map(UserBotInfo::getUserLocale).orElse(Locale.getDefault());
    }

    @Override
    public void setUserLocale(Long userChatId, Locale userLocale) {
        if (userBotMap.containsKey(userChatId))
            userBotMap.get(userChatId).setUserLocale(userLocale);
        else
            safePutToMap(userChatId, new UserBotInfo(userChatId, BotStateEnum.INIT, null, userLocale));
    }

    private void safePutToMap(Long userId, UserBotInfo userBotInfo) {
        if (!userBotMap.containsKey(userId) && userBotMap.size() >= MAX_USERS_AMOUNT)
            throw new ExceededMaximumSubscriptionsAmountException();
        userBotMap.put(userId, userBotInfo);
    }
}
