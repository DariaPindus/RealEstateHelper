package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.parsers.JsoupOfferParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParariusSource implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(ParariusSource.class);

    private static final int LAST_N_PAGES_TO_FETCH = 3;
    private static final String PARARIUS_BASE_URL = "https://www.pararius.com/apartments/rotterdam";

    private static final String MAIN_ELEMENT_CLASS = "search-list__item search-list__item--listing";

    private final JsoupOfferParser jsoupOfferParser;

    public ParariusSource(JsoupOfferParser jsoupOfferParser) {
        this.jsoupOfferParser = jsoupOfferParser;
    }

    @Override
    public List<RentalOfferDTO> getOffers() {
        List<RentalOfferDTO> resultOffers = new ArrayList<>();
        for (int i = 0; i < LAST_N_PAGES_TO_FETCH; i++) {
            try {
                Document doc = Jsoup.connect(getNextPageUrl(i)).get();
                Elements elements = doc.getElementsByClass(MAIN_ELEMENT_CLASS);
                elements.stream().map(jsoupOfferParser::parseOfferDTO).forEach(resultOffers::add);
            } catch (Exception e) {
                log.error("Exception fetching Pararius: {}", e.getMessage());
            }
        }
        log.error("Fetched {} from Pararius ", resultOffers.size());
        return resultOffers;
    }

    private String getNextPageUrl(int pageIndex) {
        return pageIndex < 1 ? PARARIUS_BASE_URL : PARARIUS_BASE_URL + "/page-" + (pageIndex + 1);
    }


}
