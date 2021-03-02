package com.daria.learn.rentalhelper.bot.notification;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;
import com.daria.learn.rentalhelper.bot.domain.OfferMessage;
import com.daria.learn.rentalhelper.bot.domain.OutboundRentalOfferDTO;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
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
    public void notifySubscribedUsers(RentalOffersListDTO rentalOffersListDTO) {
        List<BotOutgoingMessage> messagesToSend = userCache.getSubscribedUserInfos().stream()
                .map(userBotInfo -> {
                    RentalOffersListDTO personalisedOffers = getPersonalisedRentalList(rentalOffersListDTO, userBotInfo.getUserPreference());
                    if (personalisedOffers.size() < 1)
                        return null;
                    String messageText = new OfferMessage(personalisedOffers.getRentalOfferDTOS(), Instant.now()).getMessage();
                    return new BotOutgoingMessage(messageText, userBotInfo.getChatId().toString());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        notifierBot.sendMessagesToUsers(messagesToSend);
    }

    private RentalOffersListDTO getPersonalisedRentalList(RentalOffersListDTO allRentals, UserPreference userPreference) {
        if (userPreference == null)
            return allRentals;
        List<OutboundRentalOfferDTO> offersListDTOS = allRentals.getRentalOfferDTOS().stream().filter(userPreference::isMatching).collect(Collectors.toList());
        return new RentalOffersListDTO(offersListDTOS);
    }
}
