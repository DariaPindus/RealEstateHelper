package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;

import java.util.Optional;

public interface CustomRentalOfferRepository {

    Optional<RentalOffer> findExistingOffer(RentalOffer offer);

}
