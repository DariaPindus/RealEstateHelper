package com.daria.learn.rentalhelper.bot.domain;

import com.daria.learn.rentalhelper.dtos.OutboundRentalOfferDTO;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OfferMessage {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "dd/MM/yyyy HH:mm" )
            .withZone(ZoneOffset.UTC);

    private final List<OutboundRentalOfferDTO> offerDTOS;
    @Getter
    private final Instant time;
    @Getter
    private static final String parseMode = "HTML";

    public OfferMessage(List<OutboundRentalOfferDTO> offerDTOS, Instant time) {
        this.offerDTOS = offerDTOS;
        this.time = time;
    }

    public String getMessage() {
        return buildMessage().toString();
    }

    private StringBuilder buildMessage() {
        Instant.now();
        StringBuilder sb = new StringBuilder("<i>New offers by ");
        sb.append(formatter.format(time));
        sb.append("(UTC) </i>\n");

        for (int i = 0; i < offerDTOS.size(); i++) {
            sb.append("<b>");
            sb.append(i + 1);
            sb.append("</b> ");
            OutboundRentalOfferDTO offerDTO = offerDTOS.get(i);
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
            sb.append("\n");
        }
        return sb;
    }
}
