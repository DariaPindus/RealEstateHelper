package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.FilterOfInterest;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.sources.DataSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FetcherFacadeImpl implements FetcherFacade {

    private final FilterOfInterest filterOfInterest = null;
    private final Set<DataSource> dataSources;

    public FetcherFacadeImpl(Set<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public List<RentalOfferDTO> fetchOffers() {
        List<RentalOfferDTO> resultList = new LinkedList<>();
        for (DataSource dataSource : dataSources) {
            resultList.addAll(dataSource.getOffers(filterOfInterest));
        }
        return resultList;
    }
}
