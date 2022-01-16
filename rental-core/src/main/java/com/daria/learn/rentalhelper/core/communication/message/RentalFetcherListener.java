package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;

@Component
public class RentalFetcherListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);

    private final RentalOfferFacade rentalOfferFacade;
    private final ObjectMapper objectMapper;

    public RentalFetcherListener(RentalOfferFacade rentalOfferFacade, ObjectMapper objectMapper) {
        this.rentalOfferFacade = rentalOfferFacade;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.general.response}")
    public void onMessage(Message message) {
        try {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage)message;
            BriefRentalOffersListDTO briefRentalOffersListDTO = objectMapper.readValue(textMessage.getText(), BriefRentalOffersListDTO.class);
            log.info("Rental fetcher listener received request: {} ", briefRentalOffersListDTO);
            rentalOfferFacade.persistNewRentals(briefRentalOffersListDTO.getRentalOffers());
        } catch (Exception ex) {
            log.error("Couldn't process general rental message: " + message + ", ex: " + ex.getMessage());
        }
    }
}
