package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.dtos.DetailRentalOffersListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class RentalNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(RentalNotificationSender.class);

    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.topic.bot.notificaion}")
    private String topic;

    public RentalNotificationSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(DetailRentalOffersListDTO detailRentalOffersListDTO){
        try{
            log.info("Attempting Send message to Topic: "+ topic);
            jmsTemplate.send(topic, session -> session.createObjectMessage(detailRentalOffersListDTO));
        } catch(Exception e){
            log.info("Received Exception during send Message: " + e);
        }
    }
}
