package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledDetailsUpdateJob {
    private final FetcherFacade fetcherFacade;
    private final RentalPersistenceFacade rentalPersistenceFacade;

    public ScheduledDetailsUpdateJob(FetcherFacade fetcherFacade, RentalPersistenceFacade rentalPersistenceFacade) {
        this.fetcherFacade = fetcherFacade;
        this.rentalPersistenceFacade = rentalPersistenceFacade;
    }

    public void runUpdateRentalOffers() {
        List<String> urls = rentalPersistenceFacade.getOpenOffersUrls();
        fetcherFacade.fetchOfferDetail(urls);
    }
}
