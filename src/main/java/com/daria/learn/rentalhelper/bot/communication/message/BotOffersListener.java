package com.daria.learn.rentalhelper.bot.communication.message;

import com.daria.learn.rentalhelper.bot.handle.outbound.RentalBotNotifierFacade;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class BotOffersListener implements MessageListener {

    private final RentalBotNotifierFacade botNotifierFacade;

    public BotOffersListener(RentalBotNotifierFacade botNotifierFacade) {
        this.botNotifierFacade = botNotifierFacade;
    }

    @Override
    @JmsListener(destination = "${spring.activemq.topic.rental}")
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            RentalOffersListDTO offerDTOS = (RentalOffersListDTO)objectMessage.getObject();
            if (offerDTOS.size() > 0)
                botNotifierFacade.notifySubscribedUsers(offerDTOS);
        } catch(Exception e) {
            System.out.println("BotOffersListener Exception : "+ e.getMessage());
        }
    }
}
