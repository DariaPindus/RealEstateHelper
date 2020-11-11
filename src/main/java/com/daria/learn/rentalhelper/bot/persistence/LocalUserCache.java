package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.handle.UserBotInfo;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalUserCache implements UserCache {

    private final Map<Integer, UserBotInfo> userBotMap = new HashMap<>();

    @Override
    public Optional<BotStateEnum> getUserState(Integer userId) {
        return Optional.ofNullable(userBotMap.get(userId)).map(UserBotInfo::getState);
    }

    @Override
    public Optional<UserPreference> getUserPreference(Integer userId) {
        return Optional.ofNullable(userBotMap.get(userId)).map(UserBotInfo::getUserPreference);
    }

    @Override
    public void setUserPreference(Integer userId, UserPreference userPreference) {
        if (userBotMap.containsKey(userId)) {
            UserBotInfo userBotInfo = userBotMap.get(userId);
            userBotInfo.setState(BotStateEnum.SUBSCRIBED);
            userBotInfo.setUserPreference(userPreference);
        } else {
            UserBotInfo userBotInfo = new UserBotInfo(userId, BotStateEnum.SUBSCRIBED, userPreference);
            userBotMap.put(userId, userBotInfo);
        }
    }

    @Override
    public void setUserState(Integer userId, BotStateEnum stateEnum) {
        if (userBotMap.containsKey(userId)) {
            userBotMap.get(userId).setState(stateEnum);
        } else {
            userBotMap.put(userId, new UserBotInfo(userId, stateEnum, null));
        }
    }

    @Override
    //TODO: optimize (cache ?)
    public Set<Integer> getSubscribedUserIds() {
        return userBotMap.entrySet().stream()
                .filter(entry -> entry.getValue().getState() == BotStateEnum.SUBSCRIBED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
