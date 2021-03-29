package com.daria.learn.rentalhelper.dtos;

public enum RentalStatusDTO {
    AVAILABLE("available"),
    UNDER_OPTION("under_option"),
    DELETED("deleted"),
    OTHER("other");

    private String value;

    RentalStatusDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RentalStatusDTO fromValue(String status) {
        String statusLowerCase = status.toLowerCase();
        switch (statusLowerCase) {
            case "available": return RentalStatusDTO.AVAILABLE;
            case "under_option": return RentalStatusDTO.UNDER_OPTION;
            case "deleted": return RentalStatusDTO.DELETED;
            default: return RentalStatusDTO.OTHER;
        }
    }
}
