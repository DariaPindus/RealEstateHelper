package com.daria.learn.rentalhelper.rentalfetcher.parsers;

import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import org.jsoup.nodes.Element;

public interface OfferDetailParser {
    RentalOfferDetailsDTO parseOfferDetailsDTO(Element rootElement, String link);
}
