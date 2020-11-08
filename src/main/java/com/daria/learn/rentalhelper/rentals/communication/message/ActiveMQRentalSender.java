package com.daria.learn.rentalhelper.rentals.communication.message;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

@Component
public class ActiveMQRentalSender {

    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.topic}")
    private String topic;

    public ActiveMQRentalSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(RentalOffersListDTO rentalOffersListDTO){
        try{
            System.out.println("Attempting Send message to Topic: "+ topic);
            jmsTemplate.send(topic, session -> session.createObjectMessage(rentalOffersListDTO));
        } catch(Exception e){
            System.out.println("Recieved Exception during send Message: " + e);
        }
    }
}
