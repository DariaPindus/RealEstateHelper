package com.daria.learn.fetcherservice.communication;

import com.daria.learn.fetcherservice.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.List;

@Component
public class RentalFetcherListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);
    private final FetcherFacade fetcherFacade;
    private final RentalFetchSender rentalFetchSender;
    private final ObjectMapper objectMapper;

    public RentalFetcherListener(FetcherFacade fetcherFacade,
                                 RentalFetchSender rentalFetchSender,
                                 ObjectMapper objectMapper) {
        this.fetcherFacade = fetcherFacade;
        this.rentalFetchSender = rentalFetchSender;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.general.request}")
    public void onMessage(Message message) {
        try{
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage)message;
            FetchBriefRequestDTO fetchBriefRequestDTO = objectMapper.readValue(textMessage.getText(), FetchBriefRequestDTO.class);
            log.info("Rental fetcher listener received request: {} ", fetchBriefRequestDTO);
            //TODO: pagination ??
            List<BriefRentalOfferDTO> briefRentalOfferDTOS = fetchBriefRequestDTO.getDataSource() == null ?
                    fetcherFacade.fetchOffers() :
                    fetcherFacade.fetchOffersFromSource(fetchBriefRequestDTO.getDataSource());

            if (briefRentalOfferDTOS.size() > 0)
                rentalFetchSender.sendFetchedRentalOffers(new BriefRentalOffersListDTO(briefRentalOfferDTOS));
        } catch(Exception e) {
            log.error("Exception in bot offer listener: {}", e.getMessage());
        }
    }
}
