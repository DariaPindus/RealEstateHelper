package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

//@Profile(ApplicationProfiles.NAMED_QUERIES_PROFILE)
@Component
public interface JpaRentalOfferRepository extends CrudRepository<RentalOffer, Integer> {
}
