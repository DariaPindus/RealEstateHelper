package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
//@Profile(ApplicationProfiles.JPA_METHOD_PROFILE)
public class JpaMethodRentalOfferRepositoryAdapter implements RentalOfferRepository {

    private static final String IMPLEMENTATION_NAME = "JPA method";
    private final JpaMethodRentalOfferRepository jpaMethodRentalOfferRepository;

    public JpaMethodRentalOfferRepositoryAdapter(JpaMethodRentalOfferRepository jpaMethodRentalOfferRepository) {
        this.jpaMethodRentalOfferRepository = jpaMethodRentalOfferRepository;
    }

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        return jpaMethodRentalOfferRepository.findBySearchStringIn(searchStrings);
    }

    @Override
    public Optional<RentalOffer> findOfferHistoriesByOfferName(String name) {
        //throw unsupportedException("findOfferHistoryByName", IMPLEMENTATION_NAME);
        return jpaMethodRentalOfferRepository.findFirstByName(name);
    }

    @Override
    public List<RentalOffer> findAllByNameContains(String subname) {
        return jpaMethodRentalOfferRepository.findAllByNameContaining(subname);
    }

    @Override
    public List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        return jpaMethodRentalOfferRepository.findAllByAgency(name, pageable);
    }

    @Override
    public List<RentalOffer> findAllSortedByPriceAscPaged(Pageable pageable) {
        return jpaMethodRentalOfferRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("price")));
    }

    @Override
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        return jpaMethodRentalOfferRepository.findAllByPriceGreaterThanAndAreaLessThan(price, area);
    }

    @Override
    public List<RentalOffer> findAllWithHistoryUpdatedAfter(Instant time) {
        return jpaMethodRentalOfferRepository.findByOfferHistories_TimeGreaterThanAndOfferHistories_StatusIs(time, OfferStatus.UPDATED);
    }

    @Override
    public List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time) {
        throw unsupportedException("findAllModifiedAfterSortedByTimeDesc", IMPLEMENTATION_NAME);
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000() {
        throw unsupportedException("findAllPriceGrewUpInLastWeek", IMPLEMENTATION_NAME);
    }

    @Override
    public long countAll() {
        return jpaMethodRentalOfferRepository.count();
    }

    @Override
    public long countCreatedInLastMonth() {
        return jpaMethodRentalOfferRepository.countDistinctRentalOfferByOfferHistories_TimeGreaterThanAndOfferHistories_StatusIs(Instant.now().minus(30, ChronoUnit.DAYS), OfferStatus.NEW);
    }

    @Override
    public void saveList(List<RentalOffer> offers) {
        jpaMethodRentalOfferRepository.saveAll(offers);
    }

    @Override
    public String getName() {
        return "jpamethod";
    }

    @Override
    public Optional<ImmutablePair<String, Long>> getAgencyWithMostOffersLast30Days() {
        throw unsupportedException("getAgencyWithMostOffersLast30Days", IMPLEMENTATION_NAME);
    }

}
