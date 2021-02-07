package com.daria.learn.rentalhelper.rentals.domain;

import java.util.List;

public interface RentalOfferFieldNames {
    String PRICE_FIELD = "price";
    String RENTAL_STATUS_FIELD = "rental_status";
    String IS_DELETED_FIELD = "is_deleted";
    String NAME_FIELD = "name";
    String AVAILABLE_FROM_FIELD = "available_from";
    String IS_FURNISHED_FIELD = "furnished";

    static List<String> getAll() {
        return List.of(PRICE_FIELD, RENTAL_STATUS_FIELD, IS_DELETED_FIELD, NAME_FIELD, AVAILABLE_FROM_FIELD, IS_FURNISHED_FIELD);
    }
}
