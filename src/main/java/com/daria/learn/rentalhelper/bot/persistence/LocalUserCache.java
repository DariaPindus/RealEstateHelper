package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserBotInfo;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
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
    public void setUserPreferenceFromMessage(Message message, UserPreference userPreference) {
        Integer userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        if (userBotMap.containsKey(userId)) {
            UserBotInfo userBotInfo = userBotMap.get(userId);
            userBotInfo.setState(BotStateEnum.SUBSCRIBED);
            userBotInfo.setUserPreference(userPreference);
        } else {
            UserBotInfo userBotInfo = new UserBotInfo(userId, chatId, BotStateEnum.SUBSCRIBED, userPreference);
            userBotMap.put(userId, userBotInfo);
        }
    }

    @Override
    public void setUserStateFromMessage(Message message, BotStateEnum stateEnum) {
        Integer userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        if (userBotMap.containsKey(userId)) {
            userBotMap.get(userId).setState(stateEnum);
        } else {
            userBotMap.put(userId, new UserBotInfo(userId, chatId, stateEnum, null));
        }
    }

    @Override
    //TODO: optimize (cache ?)
    public List<UserBotInfo> getSubscribedUserInfos() {
        return userBotMap.values().stream()
                .filter(userBotInfo -> userBotInfo.getState() == BotStateEnum.SUBSCRIBED)
                .collect(Collectors.toList());
    }
}
