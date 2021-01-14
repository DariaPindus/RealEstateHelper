package com.daria.learn.rentalhelper.bot.persistence;

import com.daria.learn.rentalhelper.bot.domain.UserBotInfo;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;
import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RedisUserRepository implements UserRepository<Long> {
    private final String USER_HASH = "USER";

    private HashOperations<String, Long, UserBotInfo> hashOperations;

    public RedisUserRepository(RedisTemplate<String, UserBotInfo> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Optional<BotStateEnum> getUserState(@NotNull Long userChatId) {
        return Optional.ofNullable(hashOperations.get(USER_HASH, userChatId)).map(UserBotInfo::getState);
    }

    @Override
    public Optional<UserPreference> getUserPreference(@NotNull Long userChatId) {
        return Optional.ofNullable(hashOperations.get(USER_HASH, userChatId)).map(UserBotInfo::getUserPreference);
    }

    @Override
    public void setUserPreference(@NotNull Long userChatId, UserPreference userPreference) {
        UserBotInfo userBotInfo = redisContains(userChatId) ?
                hashOperations.get(USER_HASH, userChatId) : new UserBotInfo(userChatId, BotStateEnum.SUBSCRIBED, userPreference);
        userBotInfo.setState(BotStateEnum.SUBSCRIBED);
        userBotInfo.setUserPreference(userPreference);
        hashOperations.put(USER_HASH, userChatId, userBotInfo);
    }

    private boolean redisContains(@NotNull Long userChatId) {
        return hashOperations.get(USER_HASH, userChatId) != null;
    }

    @Override
    public void setUserState(@NotNull Long userChatId, BotStateEnum stateEnum) {
        UserBotInfo userBotInfo = redisContains(userChatId) ?
                hashOperations.get(USER_HASH, userChatId) : new UserBotInfo(userChatId, BotStateEnum.SUBSCRIBED, null);
        userBotInfo.setState(BotStateEnum.SUBSCRIBED);
        hashOperations.put(USER_HASH, userChatId, userBotInfo);
    }

    @Override
    //TODO: optimize (cache ?)
    public List<UserBotInfo> getSubscribedUserInfos() {
        return hashOperations.values(USER_HASH).stream()
                .filter(userBotInfo -> userBotInfo.getState() == BotStateEnum.SUBSCRIBED)
                .collect(Collectors.toList());
    }

    @Override
    public Locale getUserLocale(Long userChatId) {
        return Optional.ofNullable(hashOperations.get(USER_HASH, userChatId)).map(UserBotInfo::getUserLocale).orElse(Locale.getDefault());
    }

    @Override
    public void setUserLocale(Long userChatId, Locale userLocale) {
        UserBotInfo userBotInfo = redisContains(userChatId) ?
                hashOperations.get(USER_HASH, userChatId) : new UserBotInfo(userChatId, BotStateEnum.SUBSCRIBED, null, userLocale);
        userBotInfo.setUserLocale(userLocale);
        hashOperations.put(USER_HASH, userChatId, userBotInfo);
    }


}
