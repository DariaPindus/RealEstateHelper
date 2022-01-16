package com.daria.learn.fetcherservice.communication;

import com.daria.learn.fetcherservice.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import com.daria.learn.rentalhelper.dtos.FetchDetailRequestDTO;
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
public class RentalFetcherDetailListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);
    private final FetcherFacade fetcherFacade;
    private final RentalFetchSender rentalFetchSender;
    private final ObjectMapper objectMapper;


    public RentalFetcherDetailListener(FetcherFacade fetcherFacade, RentalFetchSender rentalFetchSender, ObjectMapper objectMapper) {
        this.fetcherFacade = fetcherFacade;
        this.rentalFetchSender = rentalFetchSender;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.detail.request}")
    public void onMessage(Message message) {
        try{
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage)message;
            FetchDetailRequestDTO fetchDetailRequestDTO = objectMapper.readValue(textMessage.getText(), FetchDetailRequestDTO.class);
            log.info("Rental fetcher detail listener received request: {} ", fetchDetailRequestDTO);
            List<DetailRentalOffersDTO> offerDetailsDTOS = fetcherFacade.fetchOfferDetailsFromSource(fetchDetailRequestDTO.getDataSource(), fetchDetailRequestDTO.getUrls());
            rentalFetchSender.sendFetchedDetailRentalOffers(new DetailRentalOffersListDTO(offerDetailsDTOS));
        } catch(Exception e) {
            log.error("Exception in bot offer listener: {}", e.getMessage());
        }
    }
}
