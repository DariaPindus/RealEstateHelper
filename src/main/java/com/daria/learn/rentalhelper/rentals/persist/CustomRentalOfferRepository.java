package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CustomRentalOfferRepository {

    @Transactional
    Optional<RentalOffer> findOfferHistoryById(Integer id);
}
