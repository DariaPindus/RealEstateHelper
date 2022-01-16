package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class RentalNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(RentalNotificationSender.class);

    private final JmsTemplate topicJmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.activemq.topic.bot.notificaion}")
    private String topic;

    public RentalNotificationSender(JmsTemplate topicJmsTemplate, ObjectMapper objectMapper) {
        this.topicJmsTemplate = topicJmsTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(DetailRentalOffersListDTO detailRentalOffersListDTO){
        try{
            log.info("Attempting Send message to Topic: "+ topic);
            String message = objectMapper.writeValueAsString(detailRentalOffersListDTO);
            topicJmsTemplate.send(topic, session -> session.createTextMessage(message));
        } catch(Exception e){
            log.info("Received Exception during send Message: " + e);
        }
    }
}
