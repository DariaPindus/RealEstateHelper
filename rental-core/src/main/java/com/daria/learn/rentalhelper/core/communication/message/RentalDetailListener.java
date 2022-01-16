package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.List;

@Component
public class RentalDetailListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);

    private final ObjectMapper objectMapper;
    private final RentalOfferFacade rentalOfferFacade;
    private final RentalNotificationSender rentalNotificationSender;

    public RentalDetailListener(ObjectMapper objectMapper,
                                RentalOfferFacade rentalOfferFacade,
                                RentalNotificationSender rentalNotificationSender) {
        this.objectMapper = objectMapper;
        this.rentalOfferFacade = rentalOfferFacade;
        this.rentalNotificationSender = rentalNotificationSender;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.detail.response}")
    public void onMessage(Message message) {
        try {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage)message;
            DetailRentalOffersListDTO rentalOfferDetailsDTO = objectMapper.readValue(textMessage.getText(), DetailRentalOffersListDTO.class);
            log.info("Rental fetcher listener received request: {} ", rentalOfferDetailsDTO);

            List<DetailRentalOffersDTO> updatedRentalDetails = rentalOfferFacade.updateRentalDetails(rentalOfferDetailsDTO.getOffersList());

            if (!updatedRentalDetails.isEmpty())
                rentalNotificationSender.sendMessage(new DetailRentalOffersListDTO(updatedRentalDetails));
        } catch (Exception ex) {
            log.error("Couldn't process general rental message: " + message + ", ex: " + ex.getMessage());
        }
    }

}
