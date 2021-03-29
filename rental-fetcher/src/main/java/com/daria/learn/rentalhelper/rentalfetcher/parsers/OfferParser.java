package com.daria.learn.rentalhelper.rentalfetcher.parsers;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import org.jsoup.nodes.Element;


public interface OfferParser {
    BriefRentalOfferDTO parseOfferDTO(Element rootElement);
}
