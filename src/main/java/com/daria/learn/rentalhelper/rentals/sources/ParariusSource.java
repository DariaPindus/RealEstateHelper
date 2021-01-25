package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.common.SSLHelper;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.rentals.parsers.OfferParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ParariusSource implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(ParariusSource.class);

    private static final int LAST_N_PAGES_TO_FETCH = 2;
    private static final String PARARIUS_BASE_URL = "https://www.pararius.com/apartments/rotterdam";

    private static final String MAIN_ELEMENT_CLASS = "search-list__item search-list__item--listing";
    private static final String DETAILS_MAIN_ELEMENT_SELECTOR = "body > main > article";

    private final OfferParser offerParser;

    public ParariusSource(OfferParser offerParser) {
        this.offerParser = offerParser;
    }

    @Override
    public List<RentalOfferDTO> getOffers() {
        List<RentalOfferDTO> resultOffers = new ArrayList<>();
        for (int i = 0; i < LAST_N_PAGES_TO_FETCH; i++) {
            try {
                Document doc = SSLHelper.getConnection(getNextPageUrl(i)).get();
                Elements elements = doc.getElementsByClass(MAIN_ELEMENT_CLASS);
                elements.stream().map(offerParser::parseOfferDTO).forEach(resultOffers::add);
            } catch (Exception e) {
                log.error("Exception fetching Pararius: {}", e.getMessage());
            }
        }
        log.error("Fetched {} from Pararius ", resultOffers.size());
        return resultOffers;
    }

    @Override
    //TODO: NOT USED??
    public List<RentalOfferDetailsDTO> getOfferDetails(List<String> urls) {
        return null;
    }

    @Override
    public Optional<RentalOfferDetailsDTO> fetchOfferDetail(String url) {
        try {
            Document doc = SSLHelper.getConnection(url).get();
            Elements elements = doc.select(DETAILS_MAIN_ELEMENT_SELECTOR);
        } catch (Exception ex) {
            log.error("ParariusSource couldn't fetch: " + url + ". Exception: " + ex.getMessage());
        }
        return Optional.empty();
    }

    private String getNextPageUrl(int pageIndex) {
        return pageIndex < 1 ? PARARIUS_BASE_URL : PARARIUS_BASE_URL + "/page-" + (pageIndex + 1);
    }


}
