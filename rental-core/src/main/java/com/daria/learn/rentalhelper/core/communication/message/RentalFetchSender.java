package com.daria.learn.rentalhelper.core.communication.message;

import com.daria.learn.rentalhelper.dtos.FetchBriefRequestDTO;
import com.daria.learn.rentalhelper.dtos.FetchDetailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class RentalFetchSender {
    private static final Logger log = LoggerFactory.getLogger(RentalFetchSender.class);

    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.topic.fetch.general.request}")
    private String topicGeneral;

    @Value("${spring.activemq.topic.fetch.detail.request}")
    private String topicDetailed;


    public RentalFetchSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    //TODO: exception hanlding in messagin??
    public void sendGeneralFetchRequest() {
        try {
            FetchBriefRequestDTO requestDTO = new FetchBriefRequestDTO();
            jmsTemplate.send(topicGeneral, session -> session.createObjectMessage(requestDTO));
        } catch (Exception ex) {
            log.error("Exception in sending message in to " + topicGeneral + ": {}", ex.getMessage());
        }
    }

    //TODO: exception hanlding in messagin??
    public void sendDetailFetchRequest(FetchDetailRequestDTO requestDTO) {
        try {
            jmsTemplate.send(topicGeneral, session -> session.createObjectMessage(requestDTO));
        } catch (Exception ex) {
            log.error("Exception in sending message in to " + topicGeneral + ": {}", ex.getMessage());
        }
    }
}
