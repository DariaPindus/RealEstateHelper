package com.daria.learn.rentalhelper.rentals.jobs;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.RentalNotificationFacade;
import com.daria.learn.rentalhelper.rentals.fetch.FetcherFacade;
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
    private final RentalNotificationFacade rentalNotificationFacade;

    public ScheduledRentalFetchingJob(FetcherFacade fetcherFacade, RentalNotificationFacade rentalNotificationFacade) {
        this.fetcherFacade = fetcherFacade;
        this.rentalNotificationFacade = rentalNotificationFacade;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchRentals() {
        System.out.println(Instant.now() + "Run fetch rentals");
        List<RentalOfferDTO> offerDTOList = fetcherFacade.fetchOffers();
        rentalNotificationFacade.saveAndNotifyNewRentals(offerDTOList);
    }
}
