package com.daria.learn.rentalhelper.bot.handle.outbound;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.model.OfferMessage;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.List;
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
                    String messageText = new OfferMessage(rentalOffersListDTO.getRentalOfferDTOS(), Instant.now()).getMessage();
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setParseMode(OfferMessage.getParseMode());
                    sendMessage.setText(messageText);
                    sendMessage.setChatId(userBotInfo.getChatId().toString());
                    return sendMessage;
                }).collect(Collectors.toList());
        notifierBot.sendMessagesToUsers(messagesToSend);
    }
}
