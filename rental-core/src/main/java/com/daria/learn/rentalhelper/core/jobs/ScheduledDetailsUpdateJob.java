package com.daria.learn.rentalhelper.core.jobs;

import com.daria.learn.rentalhelper.core.communication.message.RentalFetchSender;
import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.FetchDetailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledDetailsUpdateJob {

    private static final Logger log = LoggerFactory.getLogger(ScheduledDetailsUpdateJob.class);

    private final RentalOfferFacade rentalOfferFacade;
    private final RentalFetchSender rentalFetchSender;

    public ScheduledDetailsUpdateJob(RentalOfferFacade rentalOfferFacade, RentalFetchSender rentalFetchSender) {
        this.rentalOfferFacade = rentalOfferFacade;
        this.rentalFetchSender = rentalFetchSender;
    }

    //TODO: possibly run updates from different sources with some delay between each other
    //TODO: move to RentalNotificationFacade(Impl)?
    @Scheduled(fixedDelay = 600000)
    public void runUpdateRentalOffers() {
        try {
            List<FetchDetailRequestDTO> detailRequests = rentalOfferFacade.getFetchDetailRequests();

            for (FetchDetailRequestDTO requestDTO : detailRequests) {
                rentalFetchSender.sendDetailFetchRequest(requestDTO);
            }
        } catch (Exception ex) {
            log.error("Error sending detail fetch request: " + ex.getMessage());
        }
    }

}
