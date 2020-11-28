package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Profile(ApplicationProfiles.JPA_JPQL_PROFILE)
@Repository
@Primary
public interface JpaQueryRentalOfferRepository extends CrudRepository<RentalOffer, Integer>, RentalOfferRepository {

    @Query("select ro from RentalOffer ro where ro.searchString in ?1")
    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    @Query("select ro, ro.offerHistories from RentalOffer ro where ro.name=?1")
    Optional<RentalOffer> findOfferHistoryByName(String name);

    @Query("select ro, ro.offerHistories from RentalOffer ro where ro.name like %?1%")
    List<RentalOffer> findAllByNameContains(String subname);

    @Query("select ro, ro.offerHistories from RentalOffer ro where ro.agency=?1")
    List<RentalOffer> findAllByAgency(String name, Pageable pageable);

    @Query("select ro from RentalOffer ro where ro.name=?1 and ro.price > ?2 and ro.area < ?3")
    List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area);

    @Query("select ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1")
    List<RentalOffer> findAllUpdatedAfter(Instant time);

    @Query("select ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.fieldHistory.fieldName = ?1")
    List<RentalOffer> findAllUpdatedByFieldName(String fieldName);

    @Query("select ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1")
    List<RentalOffer> findAllPriceGrewUp(Instant since);

    @Query("select count(ro) from RentalOffer ro")
    long countAll();

    @Query("select count(ro) from RentalOffer ro where exists (select 1 from ro.offerHistories oh where oh.rentalOffer.id=ro.id and oh.time>=?1)")
    long countCreatedInLastMonth(Instant lastmonth);

    default long countCreatedInLastMonth(){
        return countCreatedInLastMonth(Instant.now().minus(1, ChronoUnit.MONTHS));
    }

    default List<RentalOffer> findAllPriceGrewUpInLastMonth() {
        return findAllPriceGrewUp(Instant.now().minus(1, ChronoUnit.MONTHS));
    }

    @Override
    default void saveList(List<RentalOffer> offers) {
        saveAll(offers);
    }

    @Override
    default List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        return findAllByAgency(name, pageable);
    }
}
