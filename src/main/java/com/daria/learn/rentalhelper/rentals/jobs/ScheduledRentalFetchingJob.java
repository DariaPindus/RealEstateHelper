package com.daria.learn.rentalhelper.rentals.jobs;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ApplicationProfiles.NOT_TEST_PROFILE)
@Component
public class ScheduledRentalFetchingJob {

    private final FetcherFacade fetcherFacade;
    private final RentalPersistenceFacade rentalPersistenceFacade;

    public ScheduledRentalFetchingJob(FetcherFacade fetcherFacade, RentalPersistenceFacade rentalPersistenceFacade) {
        this.fetcherFacade = fetcherFacade;
        this.rentalPersistenceFacade = rentalPersistenceFacade;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchRentals() {
        List<BriefRentalOfferDTO> offerDTOList = fetcherFacade.fetchOffers();
        rentalPersistenceFacade.persistNewRentals(offerDTOList);
    }
}
