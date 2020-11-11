package com.daria.learn.rentalhelper.rentals.sources;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.parsers.JsoupOfferParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParariusSource implements DataSource {

    private static final int LAST_N_PAGES_TO_FETCH = 1;
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
                System.out.println("Exception! " + e.getMessage());
            }
        }
        return resultOffers;
    }

    private String getNextPageUrl(int pageIndex) {
        return pageIndex < 1 ? PARARIUS_BASE_URL : PARARIUS_BASE_URL + "/page-" + (pageIndex + 1);
    }


}
