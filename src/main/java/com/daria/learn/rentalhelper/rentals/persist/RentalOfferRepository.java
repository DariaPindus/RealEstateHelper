package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RentalOfferRepository extends CrudRepository<RentalOffer, Integer>, CustomRentalOfferRepository {

    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

}
