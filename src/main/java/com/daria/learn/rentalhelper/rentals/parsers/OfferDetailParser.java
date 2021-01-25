package com.daria.learn.rentalhelper.rentals.parsers;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import org.jsoup.nodes.Element;

public interface OfferDetailParser {
    RentalOfferDetailsDTO parseOfferDetailsDTO(Element rootElement);
}
