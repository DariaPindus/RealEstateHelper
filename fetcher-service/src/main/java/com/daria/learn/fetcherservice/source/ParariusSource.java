package com.daria.learn.fetcherservice.source;

import com.daria.learn.fetcherservice.common.LRUCache;
import com.daria.learn.fetcherservice.common.SSLHelper;
import com.daria.learn.fetcherservice.parse.OfferDetailParser;
import com.daria.learn.fetcherservice.parse.OfferParser;
import com.daria.learn.fetcherservice.parse.ParariusOfferDetailsParser;
import com.daria.learn.fetcherservice.parse.ParariusOfferParser;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;

@Component
public class ParariusSource implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(ParariusSource.class);
    private static final String NAME = "pararius";

    private static final int LAST_N_PAGES_TO_FETCH = 3;
    private static final String CACHE_NAME = "parariussource";
    private static final String PARARIUS_BASE_URL = "https://www.pararius.com/apartments/rotterdam";

    private static final String MAIN_ELEMENT_CLASS = "search-list__item search-list__item--listing";
    private static final String DETAILS_MAIN_ELEMENT_SELECTOR = "body > main > article";

    private final OfferParser offerParser;
    private final OfferDetailParser offerDetailParser;
    private final LRUCache cache;

    public ParariusSource(LRUCache cache) {
        this.cache = cache;
        this.offerParser = new ParariusOfferParser(NAME);
        this.offerDetailParser = new ParariusOfferDetailsParser();
    }

    @Override
    public String getSourceName() {
        return NAME;
    }

    @Override
    public List<BriefRentalOfferDTO> getNewOffers() {
        List<BriefRentalOfferDTO> resultOffers = new ArrayList<>();
        for (int i = 0; i < LAST_N_PAGES_TO_FETCH; i++) {
            try {
                Document doc = SSLHelper.getConnection(getNextPageUrl(i)).get();
                Elements elements = doc.getElementsByClass(MAIN_ELEMENT_CLASS);

                var allParsedDTOs = elements.stream().map(offerParser::parseOfferDTO).collect(toUnmodifiableList());
                resultOffers.addAll(allParsedDTOs);
                //todo: decide if I really need it here, it's not very fault-tolerant, as we might not get request in case of retry calls
                //maybe caching is better?
//                var filteredDTOs = filterNewOffers(allParsedDTOs);
//                resultOffers.addAll(filteredDTOs);

//                if (filteredDTOs.size() < allParsedDTOs.size())
//                    break;
            } catch (Exception e) {
                log.error("Exception fetching Pararius: {}", e.getMessage());
            }
        }
        log.info("Fetched {} from Pararius ", resultOffers.size());
        return resultOffers;
    }

    @Override
    public DetailRentalOffersDTO fetchOfferDetail(String url) {
        try {
            Document doc = SSLHelper.getConnection(url).get();
            Elements elements = doc.select(DETAILS_MAIN_ELEMENT_SELECTOR);

            if (isNotAvailable(doc))
                return DetailRentalOffersDTO.missed(url);

            return offerDetailParser.parseOfferDetailsDTO(elements.first(), url);
        } catch (Exception ex) {
            log.error("ParariusSource couldn't fetch: " + url + ". Exception: " + ex.getMessage());
            return DetailRentalOffersDTO.missed(url);
        }
    }

    private String getNextPageUrl(int pageIndex) {
        return pageIndex < 1 ? PARARIUS_BASE_URL : PARARIUS_BASE_URL + "/page-" + (pageIndex + 1);
    }

    private List<BriefRentalOfferDTO> filterNewOffers(List<BriefRentalOfferDTO> offerDTOS) {
        return IntStream.range(0, offerDTOS.size() - 2)
                .mapToObj(i -> new ImmutablePair<>(i, calculateCompositeHash(offerDTOS.get(i), offerDTOS.get(i + 1), offerDTOS.get(i + 2))))
                .filter(indexHashPair -> !cache.contains(PARARIUS_BASE_URL, indexHashPair.right))
                .peek(indexHashPair -> cache.put(PARARIUS_BASE_URL, indexHashPair.right))
                .map(indexHashPair -> offerDTOS.get(indexHashPair.left))
                .collect(toUnmodifiableList());
    }

    private int calculateCompositeHash(BriefRentalOfferDTO... briefRentalOfferDTOs) {
        final int prime = 61;
        int resultHash = 1;
        for (BriefRentalOfferDTO briefRentalOfferDTO : briefRentalOfferDTOs) {
            resultHash = prime * resultHash + briefRentalOfferDTO.hashCode();
        }
        return resultHash;
    }

    private boolean isNotAvailable(Document doc) {
        Element notificationElement = doc.getElementsByClass("page__row page__row--notifications").first();
        if (notificationElement != null) {
            Element notificationTitleElement = notificationElement.getElementsByClass("notification__title").first();
            return notificationTitleElement != null && notificationTitleElement.text().toLowerCase().contains("not available");
        }
        return false;
    }

}
