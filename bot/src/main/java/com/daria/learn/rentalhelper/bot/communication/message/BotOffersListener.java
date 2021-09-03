package com.daria.learn.rentalhelper.bot.communication.message;

import com.daria.learn.rentalhelper.bot.notification.RentalBotNotifierFacade;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.List;
import java.util.Optional;

@Component
public class BotOffersListener {

    private static final Logger log = LoggerFactory.getLogger(BotOffersListener.class);
    private final RentalBotNotifierFacade botNotifierFacade;

    public BotOffersListener(RentalBotNotifierFacade botNotifierFacade) {
        this.botNotifierFacade = botNotifierFacade;
    }

    @JmsListener(destination = "${spring.activemq.topic.bot.notificaion}")
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            Optional<DetailRentalOffersListDTO> offerDTOS = Optional.ofNullable((DetailRentalOffersListDTO)objectMessage.getObject());
            int size = offerDTOS.map(DetailRentalOffersListDTO::getOffersList).map(List::size).orElse(0);
            log.info("Bot offer listener received {} new offers", size);
            if (size > 0)
                botNotifierFacade.notifySubscribedUsers(offerDTOS.get().getOffersList());
        } catch(Exception e) {
            log.error("Exception in bot offer listener: {}", e.getMessage());
        }
    }
}

