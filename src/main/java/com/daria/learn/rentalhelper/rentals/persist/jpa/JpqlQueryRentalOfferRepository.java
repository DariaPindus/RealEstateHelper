package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Profile(ApplicationProfiles.JPA_JPQL_PROFILE)
@Repository
@Primary
public interface JpqlQueryRentalOfferRepository extends CrudRepository<RentalOffer, Integer>, RentalOfferRepository {

    @Query("select ro from RentalOffer ro where ro.searchString in ?1")
    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories where ro.name=?1")
    Optional<RentalOffer> findOfferHistoryByName(String name);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories where ro.name like %?1%")
    List<RentalOffer> findAllByNameContains(String subname);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories where ro.agency=?1")
    List<RentalOffer> findAllByAgency(String name, Pageable pageable);

    @Query("select ro from RentalOffer ro where ro.price >= ?1 and ro.area <= ?2")
    List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.time >= ?1")
    List<RentalOffer> findAllUpdatedAfter(Instant time);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.time >= ?1 order by oh.time")
    List<RentalOffer> findAllUpdatedAfterSortedByTimeAsc(Instant time);

    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.fieldHistory.fieldName = :fieldName")
    List<RentalOffer> findThousandUpdatedByFieldName(String fieldName, Pageable pageable);

//    @Query("select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where " +
//            "(select h.id " +
//            "from OfferHistory h " +
//            "where h.fieldName=?1 and " +
//            "h.time >= ?2 and " +
//            "FUNC('TO_NUMBER', h.fieldValue) > FUNC('TO_NUMBER', h.oldValue) ")
    @Query("SELECT a FROM RentalOffer a WHERE (SELECT b FROM OfferHistory b WHERE b MEMBER OF a.offerHistories ) ")
    List<RentalOffer> findAllPriceGrewUp(String fieldName, Instant since);

    @Query("select count(ro) from RentalOffer ro")
    long countAll();

    @Query("select count(ro) from RentalOffer ro where exists (select 1 from ro.offerHistories oh where oh.rentalOffer.id=ro.id and oh.time>=?1 and oh.status=?2)")
    long countCreatedInLastMonth(Instant lastmonth, OfferStatus offerStatus);

    default long countCreatedInLastMonth(){
        return countCreatedInLastMonth(Instant.now().minus(30, ChronoUnit.DAYS), OfferStatus.NEW);
    }

    default List<RentalOffer> findAllPriceGrewUpInLastWeek() {
//        throw unsupportedException("findAllPriceGrewUpInLastWeek", "JpqlQueryRepository");
        return findAllPriceGrewUp("price", Instant.now().minus(7, ChronoUnit.DAYS));
    }

    default List<RentalOffer> findThousandUpdatedByFieldName(String fieldName) {
        return findThousandUpdatedByFieldName(fieldName, PageRequest.of(0, 1000));
    }

    @Override
    default void saveList(List<RentalOffer> offers) {
        saveAll(offers);
    }

    @Override
    default List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        return findAllByAgency(name, pageable);
    }

    @Query("select distinct ro.agency from RentalOffer ro")
    List<String> findDistinctAgencies();

    @Override
    default String getName() {
        return "jpaQuery";
    }
}
