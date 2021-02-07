package com.daria.learn.rentalhelper.rentals.parsers;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import org.jsoup.nodes.Element;


public interface OfferParser {
    BriefRentalOfferDTO parseOfferDTO(Element rootElement);
}
