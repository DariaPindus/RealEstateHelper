package com.daria.learn.rentalhelper.rentalfetcher.parsers;

import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalStatus;
import org.jsoup.nodes.Element;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParariusOfferDetailsParser extends ParariusParser implements OfferDetailParser  {

    private static final Map<String, RentalStatus> statusMap =
            Map.of("for rent", RentalStatus.AVAILABLE,
                    "under option", RentalStatus.UNDER_OPTION,
                    "deleted", RentalStatus.DELETED);

    @Override
    public RentalOfferDetailsDTO parseOfferDetailsDTO(Element rootElement, String link) {
        double price = parsePrice(rootElement);
        String name = rootElement.getElementsByClass("listing-detail-summary__title").text().replace("For rent:", "");
        Optional<RentalStatus> status = getStatus(rootElement);
        String postalCode = parsePostalCode(rootElement.getElementsByClass("listing-detail-summary__location").text());
        Boolean includingServices = parseServices(rootElement);
        Instant availableFrom = parseAvailabilityDate(rootElement);
        int area = parseArea(rootElement);
        Boolean isFurnished = parseFurnished(rootElement);
        String agency = parseAgency(rootElement);

        RentalStatusDTO statusDTO = status.map(rentalStatus -> RentalStatusDTO.fromValue(rentalStatus.getValue())).orElse(null);
        return new RentalOfferDetailsDTO(name, link, statusDTO, postalCode,
                price, includingServices, availableFrom, isFurnished, area, agency);
    }

    private double parsePrice(Element rootElement) {
        Element element = rootElement.select(".listing-detail-summary__price meta[itemprop=price]").first();
        if (element == null || element.attr("content") == null)
            return 0;
        return Double.parseDouble(element.attr("content"));
    }

    private String parseAgency(Element rootElement) {
        return rootElement.getElementsByClass("agent-summary__title-link").text();
    }

    private int parseArea(Element rootElement) {
        String areaText = rootElement.getElementsByClass("illustrated-features__description").text();
        return (int)parseNumber(areaText);
    }

    private Boolean parseFurnished(Element rootElement) {
        Element furnishedElement = rootElement.getElementsByClass("listing-features__description listing-features__description--specifics").first();
        if (furnishedElement == null)
            return null;
        String text = furnishedElement.text().toLowerCase();
        return text.contains("furnished");
    }

    private Instant parseAvailabilityDate(Element rootElement) {
        Element availabilityElement = rootElement.getElementsByClass("listing-features__description listing-features__description--acceptance").first();
        if (availabilityElement == null)
            return null;
        String text = availabilityElement.text().toLowerCase().replace("from", "").trim();
        String[] dates = text.split("-");
        if (dates.length != 3)
            return null;
        return LocalDate.of(Integer.parseInt(dates[2]),Integer.parseInt(dates[1]), Integer.parseInt(dates[0])).atStartOfDay().toInstant(ZoneOffset.UTC); //TODO: test
    }

    private String parsePostalCode(String location) {
        Pattern pattern = Pattern.compile(POSTAL_CODE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(location);
        return matcher.find() ? matcher.group() : "";
    }

    @Nullable
    private Boolean parseServices(Element rootElement) {
        Element descriptionElement = rootElement.getElementsByClass("listing-features__sub-description").first();
        if (descriptionElement == null)
            return null;
        String descrText = descriptionElement.text();
        return descrText.toLowerCase().contains("includes");
    }

    private Optional<RentalStatus> getStatus(Element rootElement) {
        String text = rootElement.getElementsByClass("listing-features__description listing-features__description--status").text();
        if (text == null || text.isEmpty())
            return Optional.empty();
        String formatted = text.trim().toLowerCase();
        return Optional.ofNullable(statusMap.get(formatted));
    }
}
