package com.daria.learn.rentalhelper.rentalfetcher.domain;

public enum  RentalStatus {
    AVAILABLE("available"),
    UNDER_OPTION("under_option"),
    DELETED("deleted"),
    OTHER("other");

    private String value;

    RentalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RentalStatus fromValue(String status) {
        String statusLowerCase = status.toLowerCase();
        switch (statusLowerCase) {
            case "available": return RentalStatus.AVAILABLE;
            case "under_option": return RentalStatus.UNDER_OPTION;
            case "deleted": return RentalStatus.DELETED;
            default: return RentalStatus.OTHER;
        }
    }
}
