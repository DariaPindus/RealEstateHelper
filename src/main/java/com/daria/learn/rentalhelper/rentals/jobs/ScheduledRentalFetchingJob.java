package com.daria.learn.rentalhelper.rentals.jobs;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import com.daria.learn.rentalhelper.rentals.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.rentals.communication.message.ActiveMQRentalSender;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Profile(ApplicationProfiles.NOT_TEST_PROFILE)
@Component
public class ScheduledRentalFetchingJob {

    private final FetcherFacade fetcherFacade;
    private final ActiveMQRentalSender rentalSender;

    public ScheduledRentalFetchingJob(FetcherFacade fetcherFacade, ActiveMQRentalSender rentalSender) {
        this.fetcherFacade = fetcherFacade;
        this.rentalSender = rentalSender;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchRentals() {
        System.out.println(Instant.now() + "Run fetch rentals");
        List<RentalOfferDTO> offerDTOList = fetcherFacade.fetchOffers();
        rentalSender.sendMessage(new RentalOffersListDTO(offerDTOList));
    }
}
