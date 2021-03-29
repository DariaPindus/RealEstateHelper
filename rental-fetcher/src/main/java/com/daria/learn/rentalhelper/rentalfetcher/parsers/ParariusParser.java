package com.daria.learn.rentalhelper.rentalfetcher.parsers;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ParariusParser {

    protected static final String POSTAL_CODE_PATTERN = "([0-9]{4}\\s[A-Z]{2})";
    protected static final String PRICE_PATTERN = "\\b([0-9,]*)\\b";
    protected static final String PARARIUS_LINK_PREFIX = "https://www.pararius.com";

    protected double parseNumber(String price) {
        try {
            Pattern pattern = Pattern.compile(PRICE_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(price);
            return matcher.find() ? NumberFormat.getNumberInstance(Locale.UK).parse(matcher.group()).doubleValue() : 0;
        } catch (Exception ex) {
            System.out.println("exception [parsePrice] " + ex.getMessage());
            return 0;
        }
    }

}
