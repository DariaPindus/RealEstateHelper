package com.daria.learn.rentalhelper.bot.notification;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.model.OfferMessage;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RentalBotNotifierFacadeImpl implements RentalBotNotifierFacade {

    private final RentalNotifierBot notifierBot;
    private final UserCache userCache;

    public RentalBotNotifierFacadeImpl(RentalNotifierBot notifierBot, UserCache userCache) {
        this.notifierBot = notifierBot;
        this.userCache = userCache;
    }

    @Override
    public void notifySubscribedUsers(RentalOffersListDTO rentalOffersListDTO) {
        List<SendMessage> messagesToSend = userCache.getSubscribedUserInfos().stream()
                .map(userBotInfo -> {
                    RentalOffersListDTO personalisedOffers = getPersonalisedRentalList(rentalOffersListDTO, userBotInfo.getUserPreference());
                    if (personalisedOffers.size() < 1)
                        return null;
                    String messageText = new OfferMessage(rentalOffersListDTO.getRentalOfferDTOS(), Instant.now()).getMessage();
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setParseMode(OfferMessage.getParseMode());
                    sendMessage.setText(messageText);
                    sendMessage.setChatId(userBotInfo.getChatId().toString());
                    return sendMessage;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        notifierBot.sendMessagesToUsers(messagesToSend);
    }

    private RentalOffersListDTO getPersonalisedRentalList(RentalOffersListDTO allRentals, UserPreference userPreference) {
        if (userPreference == null)
            return allRentals;
        List<RentalOfferDTO> offersListDTOS = allRentals.getRentalOfferDTOS().stream().filter(userPreference::isMatching).collect(Collectors.toList());
        return new RentalOffersListDTO(offersListDTOS);
    }
}
