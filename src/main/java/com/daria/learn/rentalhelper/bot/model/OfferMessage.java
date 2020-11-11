package com.daria.learn.rentalhelper.bot.model;

import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

public class OfferMessage {

    private final List<RentalOfferDTO> offerDTOS;
    @Getter
    private final Instant time;
    @Getter
    private static final String parseMode = "HTML";

    public OfferMessage(List<RentalOfferDTO> offerDTOS, Instant time) {
        this.offerDTOS = offerDTOS;
        this.time = time;
    }

    public String getMessage() {
        return buildMessage().toString();
    }

    private StringBuilder buildMessage() {
        StringBuilder sb = new StringBuilder("<p><i>Новые предложения к ");
        sb.append(time);
        sb.append("</i></p>");

        for (int i = 0; i < offerDTOS.size(); i++) {
            sb.append("<b>");
            sb.append(i + 1);
            sb.append("</b> ");
            RentalOfferDTO offerDTO = offerDTOS.get(i);
            sb.append("<a href=\"");
            sb.append(offerDTO.getLink());
            sb.append("\">");
            sb.append(offerDTO.getName());
            sb.append("</a>");
            sb.append("  Address: ");
            sb.append(offerDTO.getPostalCode());
            sb.append(offerDTO.isFurnished() ? ". Furnished. " : ". Not furnished. ");
            sb.append(offerDTO.getArea());
            sb.append(" m2. ");
            sb.append("Price: ");
            sb.append(offerDTO.getPrice());
            sb.append(" euros. ");
            sb.append("Agency: ");
            sb.append(offerDTO.getAgency());

        }
        return sb;
    }
}
