package com.daria.learn.rentalhelper.rentals.communication.message;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

@Component
public class ActiveMQRentalListener implements MessageListener {

    private final RentalPersistenceFacade persistenceFacade;

    public ActiveMQRentalListener(RentalPersistenceFacade persistenceFacade) {
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    @JmsListener(destination = "${spring.activemq.topic}")
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            RentalOffersListDTO offerDTOS = (RentalOffersListDTO)objectMessage.getObject();
            persistenceFacade.persistRentals(offerDTOS.getRentalOfferDTOS());
        } catch(Exception e) {
            System.out.println("Received Exception : "+ e);
        }
    }
}
