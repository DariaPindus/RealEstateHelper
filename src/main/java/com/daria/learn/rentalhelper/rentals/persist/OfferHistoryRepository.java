package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OfferHistoryRepository extends CrudRepository<OfferHistory, Integer> {
    List<OfferHistory> findByRentalOffer_IdIn(Collection<Integer> rentalIds);
}
