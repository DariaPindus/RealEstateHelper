package com.daria.learn.fetcherservice.parse;

import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import org.jsoup.nodes.Element;

public interface OfferDetailParser {
    DetailRentalOffersDTO parseOfferDetailsDTO(Element rootElement, String link);
}
