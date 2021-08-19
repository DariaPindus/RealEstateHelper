package com.daria.learn.fetcherservice.communication;

import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class RentalFetchSender {
    private static final Logger log = LoggerFactory.getLogger(RentalFetchSender.class);

    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.topic.fetch.general.response}")
    private String topicGeneral;

    @Value("${spring.activemq.topic.fetch.detail.response}")
    private String topicDetailed;

    public RentalFetchSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendFetchedRentalOffers(BriefRentalOffersListDTO briefRentalOffersListDTO){
        try{
            log.info("Attempting Send message to Topic: "+ topicGeneral);
            jmsTemplate.send(topicGeneral, session -> session.createObjectMessage(briefRentalOffersListDTO));
        } catch(Exception e){
            log.info("Received Exception during send Message: " + e);
        }
    }

    public void sendFetchedDetailRentalOffers(DetailRentalOffersListDTO detailRentalOffersListDTO){
        try{
            log.info("Attempting Send message to Topic: "+ topicDetailed);
            jmsTemplate.send(topicDetailed, session -> session.createObjectMessage(detailRentalOffersListDTO));
        } catch(Exception e){
            log.info("Received Exception during send Message: " + e);
        }
    }

}
