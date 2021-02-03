package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class ScheduledDetailsUpdateJob {
    private static final Logger log = LoggerFactory.getLogger(ScheduledDetailsUpdateJob.class);

    private final FetcherFacade fetcherFacade;
    private final RentalPersistenceFacade rentalPersistenceFacade;
    private final ExecutorService executorService;

    public ScheduledDetailsUpdateJob(FetcherFacade fetcherFacade, RentalPersistenceFacade rentalPersistenceFacade) {
        this.fetcherFacade = fetcherFacade;
        this.rentalPersistenceFacade = rentalPersistenceFacade;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Scheduled(fixedRate = 60000)
    //TODO: possibly run updates from different sources with some delay between each other
    public void runUpdateRentalOffers() {
        fetcherFacade.getDataSourcesNames().forEach(this::updateSource);
    }

    private void updateSource(String source) {
        List<String> urls = rentalPersistenceFacade.getSourceOpenOffersUrls(source);
        List<RentalOfferDetailsDTO> rentalOfferDetailsDTOS = parallelizedFetchFromSource(urls, source);
        rentalPersistenceFacade.
    }

    private List<RentalOfferDetailsDTO> parallelizedFetchFromSource(List<String> urls, String source){
        try {
            List<Callable<RentalOfferDetailsDTO>> callables = new LinkedList<>();
            for (String url : urls) {
                callables.add(() -> fetcherFacade.fetchOfferDetailFromSource(url, source));
            }
            List<Future<RentalOfferDetailsDTO>> futures = executorService.invokeAll(callables);

            return futures.stream()
                    .map(this::getDetails)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<RentalOfferDetailsDTO> getDetails(Future<RentalOfferDetailsDTO> future) {
        try {
            return Optional.of(future.get());
        } catch (Exception e) {
            log.error("Error running future rental detail fetch: " + e.getMessage());
            return Optional.empty();
        }
    }
}
