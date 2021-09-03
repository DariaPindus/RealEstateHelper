package com.daria.learn.rentalhelper.core.persist;

import com.daria.learn.rentalhelper.core.domain.RentalOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalOfferRepository extends CrudRepository<RentalOffer, Integer> {

    List<RentalOffer> findBySource(String source);

    @Query("SELECT DISTINCT ro FROM RentalOffer ro left join fetch ro.offerHistories oh where ro.link in ?1")
    List<RentalOffer> findByLinkIn(Collection<String> searchLinks);

    @Query("SELECT DISTINCT ro FROM RentalOffer ro left JOIN FETCH ro.offerHistories offerHistories where ro.id=?1")
    Optional<RentalOffer> findByIdWithOfferHistories(Integer id);
}
