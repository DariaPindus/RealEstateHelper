package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RentalOfferRepository extends CrudRepository<RentalOffer, Integer>, CustomRentalOfferRepository {

    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    @Query("select ro.link from RentalOffer ro where ro.id not in (" +
            "select distinct oh.rentalOffer from FieldHistory oh " +
            "where oh.status=com.daria.learn.rentalhelper.rentals.domain.OfferStatus.REMOVED) and ro.source=?1")
    List<RentalOffer> findOpenRentalOffers(String source);

    List<RentalOffer> findByLinkIn(Collection<String> searchLinks);
}
