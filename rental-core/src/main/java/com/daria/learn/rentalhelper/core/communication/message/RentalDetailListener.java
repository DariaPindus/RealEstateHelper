package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

@Component
public class RentalDetailListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RentalGeneralListener.class);

    private final RentalOfferFacade rentalOfferFacade;
    private final RentalNotificationSender rentalNotificationSender;

    public RentalDetailListener(RentalOfferFacade rentalOfferFacade, RentalNotificationSender rentalNotificationSender) {
        this.rentalOfferFacade = rentalOfferFacade;
        this.rentalNotificationSender = rentalNotificationSender;
    }

    @Override
    @JmsListener(destination = "${spring.activemq.topic.fetch.detail.response}")
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            DetailRentalOffersListDTO rentalOfferDetailsDTO = (DetailRentalOffersListDTO) objectMessage.getObject();
            log.info("Rental fetcher listener received request: {} ", rentalOfferDetailsDTO);
            List<DetailRentalOffersDTO> updatedRentalDetails = rentalOfferFacade.updateRentalDetails(rentalOfferDetailsDTO.getOffersList());
            rentalNotificationSender.sendMessage(new DetailRentalOffersListDTO(updatedRentalDetails));
        } catch (Exception ex) {
            log.error("Couldn't process general rental message: " + message + ", ex: " + ex.getMessage());
        }
    }

}
