package com.daria.learn.rentalhelper.rentals.domain;

public enum RentalStatus {
    AVAILABLE("available"),
    UNDER_OPTION("under_option"),
    DELETED("deleted");

    private String value;

    RentalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
