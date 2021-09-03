package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;

//TODO: inheritance ?? (parent class)
@Component
public class RentalFetcherListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);

    private final RentalOfferFacade rentalOfferFacade;

    public RentalFetcherListener(RentalOfferFacade rentalOfferFacade) {
        this.rentalOfferFacade = rentalOfferFacade;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.general.response}")
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage)message;
            BriefRentalOffersListDTO briefRentalOffersListDTO = (BriefRentalOffersListDTO) objectMessage.getObject();
            log.info("Rental fetcher listener received request: {} ", briefRentalOffersListDTO);
            rentalOfferFacade.persistNewRentals(briefRentalOffersListDTO.getRentalOffers());
        } catch (Exception ex) {
            log.error("Couldn't process general rental message: " + message + ", ex: " + ex.getMessage());
        }
    }
}
