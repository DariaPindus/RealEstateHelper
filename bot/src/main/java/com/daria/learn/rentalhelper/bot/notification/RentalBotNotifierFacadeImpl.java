package com.daria.learn.rentalhelper.bot.notification;

import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;
import com.daria.learn.rentalhelper.bot.domain.OfferMessage;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RentalBotNotifierFacadeImpl implements RentalBotNotifierFacade {

    private final RentalNotifierBot notifierBot;
    private final UserRepository<Long> userCache;

    public RentalBotNotifierFacadeImpl(RentalNotifierBot notifierBot, UserRepository<Long> userCache) {
        this.notifierBot = notifierBot;
        this.userCache = userCache;
    }

    @Override
    public void notifySubscribedUsers(List<DetailRentalOffersDTO> rentalOffersListDTO) {
        List<BotOutgoingMessage> messagesToSend = userCache.getSubscribedUserInfos().stream()
                .map(userBotInfo -> {
                    List<DetailRentalOffersDTO> personalisedOffers = getPersonalisedRentalList(rentalOffersListDTO, userBotInfo.getUserPreference());
                    if (personalisedOffers.isEmpty())
                        return null;
                    String messageText = new OfferMessage(personalisedOffers, Instant.now()).getMessage();
                    return new BotOutgoingMessage(messageText, userBotInfo.getChatId().toString());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        notifierBot.sendMessagesToUsers(messagesToSend);
    }

    private List<DetailRentalOffersDTO> getPersonalisedRentalList(List<DetailRentalOffersDTO> allRentals, UserPreference userPreference) {
        if (userPreference == null)
            return allRentals;
        List<DetailRentalOffersDTO> offersListDTOS = allRentals.stream().filter(userPreference::isMatching).collect(Collectors.toList());
        return offersListDTOS;
    }
}
