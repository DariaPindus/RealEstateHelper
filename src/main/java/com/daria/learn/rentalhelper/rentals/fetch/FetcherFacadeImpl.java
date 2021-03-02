package com.daria.learn.rentalhelper.rentals.fetch;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.rentals.sources.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FetcherFacadeImpl implements FetcherFacade {

    private static final Logger log = LoggerFactory.getLogger(FetcherFacadeImpl.class);

    private final Map<String, DataSource> dataSources;

    public FetcherFacadeImpl(Set<DataSource> dataSources) {
        this.dataSources = dataSources.stream()
                .collect(Collectors.toMap(DataSource::getSourceName, (dataSource) -> dataSource));
    }

    @Override
    public List<BriefRentalOfferDTO> fetchOffers() {
        log.info("Rental fetcher to run");
        List<BriefRentalOfferDTO> resultList = new LinkedList<>();
        for (DataSource dataSource : dataSources.values()) {
            resultList.addAll(dataSource.getOffers());
        }
        logParsedOffers(resultList);
        return resultList;
    }

    public RentalOfferDetailsDTO fetchOfferDetailFromSource(String source, String url) {
        return dataSources.get(source).fetchOfferDetail(url);
    }

    @Override
    public Set<String> getDataSourcesNames() {
        return dataSources.keySet();
    }

    private void logParsedOffers(List<BriefRentalOfferDTO> resultList) {
        log.info("Rental offers fetched from data sources: \n" +
                resultList.stream().map(BriefRentalOfferDTO::toString).collect(Collectors.joining("\n")));
    }

}
