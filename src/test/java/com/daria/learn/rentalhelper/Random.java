package com.daria.learn.rentalhelper;

public class Random {

    private static final String chars = "abcdefghjklmnopqrstuyxvwz";
    private static final String uppercase = "ABCDEFGHJKLMOPQRSTUYXWZ";
    private static final String numbers = "1234567890";

    public static String getRandomOfferName() {
        boolean isApartment = new java.util.Random().nextBoolean();
        return (isApartment ? "Apartment " : "Room ") + getRandomString(0);
    }

    public static String getRandomString(int length) {
        java.util.Random r = new java.util.Random();
        int actLength = length == 0 ? r.nextInt(10) + 3 : length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < actLength; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getRandomPostalCode() {
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 4; i++) {
            sb.append(numbers.charAt(r.nextInt(numbers.length() - 1)));
        }
        sb.append(" ");
        for (int i = 0; i < 2; i++) {
            sb.append(uppercase.charAt(r.nextInt(uppercase.length() - 1)));
        }
        return sb.toString();
    }

    public static int getRandomNumber(int min, int max) {
        java.util.Random r = new java.util.Random();
        return r.nextInt(max) + min;
    }
}
