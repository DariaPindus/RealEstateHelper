package com.daria.learn.rentalhelper.rentals.parsers;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParariusOfferParser implements OfferParser {
    private final String sourceName;

    private static final String POSTAL_CODE_PATTERN = "([0-9]{4}\\s[A-Z]{2})";
    private static final String PRICE_PATTERN = "\\b([0-9,]*)\\b";
    private static final String PARARIUS_LINK_PREFIX = "https://www.pararius.com";

    private static final String NAME_CLASS = "listing-search-item__title";
    private static final String LINK_CLASS = "listing-search-item__link";
    private static final String LOCATION_CLASS = "listing-search-item__location";
    private static final String AREA_CLASS = "illustrated-features__description";
    private static final String PRICE_CLASS = "listing-search-item__price";
    private static final String AGENCY_CLASS = "listing-search-item__info";

    public ParariusOfferParser(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public BriefRentalOfferDTO parseOfferDTO(Element rootElement) {
        String name = rootElement.getElementsByClass(NAME_CLASS).text();
        String location = parsePostalCode(rootElement.getElementsByClass(LOCATION_CLASS).text());
        double price = parseNumber(rootElement.getElementsByClass(PRICE_CLASS).text());
        //TODO: furnished
        String agency = rootElement.getElementsByClass(AGENCY_CLASS).text();
        String link = parseLink(rootElement.getElementsByClass(NAME_CLASS));
        int area = parseArea(rootElement.getElementsByClass(AREA_CLASS));
        return new BriefRentalOfferDTO(name, location, area, agency, link, sourceName);
    }

    private int parseArea(Elements elementsByClass) {
        Element areaElement = elementsByClass.first();
        String areaStr = areaElement.text();
        return (int)parseNumber(areaStr);
    }

    private String parsePostalCode(String location) {
        Pattern pattern = Pattern.compile(POSTAL_CODE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(location);
        return matcher.find() ? matcher.group() : "";
    }

    private double parseNumber(String price) {
        try {
            Pattern pattern = Pattern.compile(PRICE_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(price);
            return matcher.find() ? NumberFormat.getNumberInstance(Locale.UK).parse(matcher.group()).doubleValue() : 0;
        } catch (Exception ex) {
            System.out.println("exception [parsePrice] " + ex.getMessage());
            return 0;
        }
    }

    private String parseLink(Elements titleElements) {
        if (titleElements.size() == 0 || titleElements.get(0).childrenSize() < 1)
            return "";
        return PARARIUS_LINK_PREFIX + titleElements.get(0).child(0).attr("href");
    }
}
