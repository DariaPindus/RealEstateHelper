package com.daria.learn.fetcherservice.fetch;

import com.daria.learn.fetcherservice.source.DataSource;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class FetcherFacadeImpl implements FetcherFacade {

    private static final Logger log = LoggerFactory.getLogger(FetcherFacadeImpl.class);

    private final Map<String, DataSource> dataSources;
    private final ExecutorService executorService;

    public FetcherFacadeImpl(Set<DataSource> dataSources) {
        this.dataSources = dataSources.stream()
                .collect(Collectors.toMap(DataSource::getSourceName, (dataSource) -> dataSource));
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public List<BriefRentalOfferDTO> fetchOffersFromSource(String source) {
        DataSource dataSource = Optional.ofNullable(dataSources.get(source)).orElseThrow(() -> new IllegalArgumentException("Data source " + source + " is not supported" ));
        return dataSource.getOffers();
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

    @Override
    public List<DetailRentalOffersDTO> fetchOfferDetailsFromSource(String source, List<String> urls) {
        List<DetailRentalOffersDTO> detailRentalOffersDTOS = parallelizedFetchFromSource(urls, source);
        return detailRentalOffersDTOS;
    }

    private DetailRentalOffersDTO fetchOfferDetailFromSource(String source, String url) {
        DataSource dataSource = Optional.ofNullable(dataSources.get(source)).orElseThrow(() -> new IllegalArgumentException());
        return dataSource.fetchOfferDetail(url);
    }

    private List<DetailRentalOffersDTO> parallelizedFetchFromSource(List<String> urls, String source){
        try {
            List<Callable<DetailRentalOffersDTO>> callables = new LinkedList<>();
            for (String url : urls) {
                callables.add(() -> fetchOfferDetailFromSource(source, url));
            }
            List<Future<DetailRentalOffersDTO>> futures = executorService.invokeAll(callables);

            return futures.stream()
                    .map(this::getDetails)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in paralized fetch from source: " + source + ": " + e.getMessage());
            return List.of();
        }
    }

    private Optional<DetailRentalOffersDTO> getDetails(Future<DetailRentalOffersDTO> future) {
        try {
            return Optional.of(future.get());
        } catch (Exception e) {
            log.error("Error running future rental detail fetch: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void logParsedOffers(List<BriefRentalOfferDTO> resultList) {
        log.info("Rental offers fetched from data sources: \n" +
                resultList.stream().map(BriefRentalOfferDTO::toString).collect(Collectors.joining("\n")));
    }

}
