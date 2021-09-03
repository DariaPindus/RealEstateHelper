package com.daria.learn.fetcherservice.communication;

import com.daria.learn.fetcherservice.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.List;

@Component
public class RentalFetcherListener {
    private static final Logger log = LoggerFactory.getLogger(RentalFetcherListener.class);
    private final FetcherFacade fetcherFacade;
    private final RentalFetchSender rentalFetchSender;

    public RentalFetcherListener(FetcherFacade fetcherFacade, RentalFetchSender rentalFetchSender) {
        this.fetcherFacade = fetcherFacade;
        this.rentalFetchSender = rentalFetchSender;
    }

    @JmsListener(destination = "${spring.activemq.topic.fetch.general.request}")
    public void onMessage(Message message) {
        try{
            ObjectMessage objectMessage = (ObjectMessage)message;
            FetchBriefRequestDTO fetchBriefRequestDTO = (FetchBriefRequestDTO)objectMessage.getObject();
            log.info("Rental fetcher listener received request: {} ", fetchBriefRequestDTO);
            //TODO: pagination ??
            List<BriefRentalOfferDTO> briefRentalOfferDTOS = fetchBriefRequestDTO.getDataSource() == null ?
                    fetcherFacade.fetchOffers() :
                    fetcherFacade.fetchOffersFromSource(fetchBriefRequestDTO.getDataSource());
            rentalFetchSender.sendFetchedRentalOffers(new BriefRentalOffersListDTO(briefRentalOfferDTOS));
        } catch(Exception e) {
            log.error("Exception in bot offer listener: {}", e.getMessage());
        }
    }
}
