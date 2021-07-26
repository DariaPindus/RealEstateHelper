package com.daria.learn.rentalhelper.rentalfetcher.persist;

import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalOfferRepository extends CrudRepository<RentalOffer, Integer> {

    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    @Query("select ro from RentalOffer ro where ro.rentalStatus=com.daria.learn.rentalhelper.rentalfetcher.domain.RentalStatus.AVAILABLE")
    List<RentalOffer> findOpenRentalOffers(String source);

    @Query("SELECT DISTINCT ro FROM RentalOffer ro left join fetch ro.offerHistories oh where ro.link in ?1")
    List<RentalOffer> findByLinkIn(Collection<String> searchLinks);

    @Query("SELECT DISTINCT ro FROM RentalOffer ro left JOIN FETCH ro.offerHistories offerHistories where ro.id=?1")
    Optional<RentalOffer> findByIdWithOfferHistories(Integer id);

    List<RentalOffer> findAllByCreationTimeAfter(Instant time);
}
