package com.daria.learn.rentalhelper.rentals.persist.hibernate;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//TODO
public class NativeScalarSQLRentalOfferRepository implements RentalOfferRepository {

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        return null;
    }

    @Override
    public Optional<RentalOffer> findOfferHistoriesByOfferName(String name) {
        return Optional.empty();
    }

    @Override
    public List<RentalOffer> findAllByNameContains(String subname) {
        return null;
    }

    @Override
    public List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        return null;
    }

    @Override
    public List<RentalOffer> findAllSortedByPriceAscPaged(Pageable pageable) {
        return null;
    }

    @Override
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        return null;
    }

    @Override
    public List<RentalOffer> findAllWithHistoryUpdatedAfter(Instant time) {
        return null;
    }

    @Override
    public List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time) {
        return null;
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000() {
        return null;
    }

    @Override
    public long countAll() {
        return 0;
    }

    @Override
    public long countCreatedInLastMonth() {
        return 0;
    }

    @Override
    public void saveList(List<RentalOffer> offers) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Optional<ImmutablePair<String, Long>> getAgencyWithMostOffersLast30Days() {
        return Optional.empty();
    }
}
