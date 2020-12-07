package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
//@Profile(ApplicationProfiles.JPA_METHOD_PROFILE)
public interface JpaMethodRentalOfferRepository extends CrudRepository<RentalOffer, Integer> {

    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    Optional<RentalOffer> findOfferHistoryById(int id);

    List<RentalOffer> findAllByNameContaining(String name);

    @EntityGraph(attributePaths = {"offerHistories"})
    Optional<RentalOffer> findFirstByName(String name);

    List<RentalOffer> findAllByAgency(String name, Pageable pageable);

    List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area);

    @EntityGraph(attributePaths = {"offerHistories"})
    List<RentalOffer> findByOfferHistories_TimeGreaterThan(Instant time);

    @EntityGraph(attributePaths = {"offerHistories"})
    List<RentalOffer> findFirst1000ByOfferHistories_FieldHistoryFieldNameIs(String fieldName);

    long countByOfferHistories_TimeGreaterThanAndOfferHistories_StatusIs(Instant time, OfferStatus status);
}
