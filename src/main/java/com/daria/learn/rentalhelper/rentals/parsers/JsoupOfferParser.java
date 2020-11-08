package com.daria.learn.rentalhelper.rentals.parsers;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.jsoup.nodes.Element;


public interface JsoupOfferParser {
    RentalOfferDTO parseOfferDTO(Element rootElement);
}
