package com.daria.learn.fetcherservice.communication;

import com.daria.learn.rentalhelper.dtos.BriefRentalOffersListDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class RentalFetchSender {
    private static final Logger log = LoggerFactory.getLogger(RentalFetchSender.class);

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.activemq.topic.fetch.general.response}")
    private String topicGeneral;

    @Value("${spring.activemq.topic.fetch.detail.response}")
    private String topicDetailed;

    public RentalFetchSender(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendFetchedRentalOffers(BriefRentalOffersListDTO briefRentalOffersListDTO){
        try{
            log.info("Attempting Send message to Topic: "+ topicGeneral);
            String message = objectMapper.writeValueAsString(briefRentalOffersListDTO);
            jmsTemplate.send(topicGeneral, session -> session.createTextMessage(message));
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
