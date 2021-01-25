package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.sources.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FetcherFacadeImpl implements FetcherFacade {

    private static final Logger log = LoggerFactory.getLogger(FetcherFacadeImpl.class);

    private final Set<DataSource> dataSources;

    public FetcherFacadeImpl(Set<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public List<RentalOfferDTO> fetchOffers() {
        log.info("Rental fetcher to run");
        List<RentalOfferDTO> resultList = new LinkedList<>();
        for (DataSource dataSource : dataSources) {
            resultList.addAll(dataSource.getOffers());
        }
        logParsedOffers(resultList);
        return resultList;
    }

    private void logParsedOffers(List<RentalOfferDTO> resultList) {
        log.info("Rental offers fetched from data sources: \n" +
                resultList.stream().map(RentalOfferDTO::toString).collect(Collectors.joining("\n")));
    }
}
