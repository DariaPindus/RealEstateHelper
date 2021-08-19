package com.daria.learn.fetcherservice.communication;

import com.daria.learn.fetcherservice.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

@Component
public class RentalFetcherDetailListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);
    private final FetcherFacade fetcherFacade;
    private final RentalFetchSender rentalFetchSender;

    public RentalFetcherDetailListener(FetcherFacade fetcherFacade, RentalFetchSender rentalFetchSender) {
        this.fetcherFacade = fetcherFacade;
        this.rentalFetchSender = rentalFetchSender;
    }
    @Override
    @JmsListener(destination = "${spring.activemq.topic.fetch.detail.request}")
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            FetchDetailRequestDTO fetchBriefRequestDTO = (FetchDetailRequestDTO)objectMessage.getObject();
            log.info("Rental fetcher detail listener received request: {} ", fetchBriefRequestDTO);
            List<DetailRentalOffersDTO> offerDetailsDTOS = fetcherFacade.fetchOfferDetailsFromSource(fetchBriefRequestDTO.getDataSource(), fetchBriefRequestDTO.getUrls());
            rentalFetchSender.sendFetchedDetailRentalOffers(new DetailRentalOffersListDTO(offerDetailsDTOS));
        } catch(Exception e) {
            log.error("Exception in bot offer listener: {}", e.getMessage());
        }
    }}
