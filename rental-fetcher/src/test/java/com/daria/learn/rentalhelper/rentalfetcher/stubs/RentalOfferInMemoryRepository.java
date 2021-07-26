package com.daria.learn.rentalhelper.rentalfetcher.stubs;

import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalStatus;
import com.daria.learn.rentalhelper.rentalfetcher.persist.RentalOfferRepository;

import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class RentalOfferInMemoryRepository extends InMemoryRepository<RentalOffer, Integer> implements RentalOfferRepository {

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        return storage.values().stream().filter(rentalOffer -> searchStrings.contains(rentalOffer.getSearchString())).collect(toList());
    }

    @Override
    public List<RentalOffer> findOpenRentalOffers(String source) {
        return storage.values().stream().filter(rentalOffer -> rentalOffer.getRentalStatus() == RentalStatus.AVAILABLE).collect(toList());
    }

    @Override
    public List<RentalOffer> findByLinkIn(Collection<String> searchLinks) {
        return storage.values().stream().filter(rentalOffer -> searchLinks.contains(rentalOffer.getSearchString())).collect(toList());
    }

    @Override
    public Optional<RentalOffer> findByIdWithOfferHistories(Integer id) {
        return findById(id);
    }

    @Override
    public List<RentalOffer> findAllByCreationTimeAfter(Instant time) {
        return storage.values().stream().filter(rentalOffer -> rentalOffer.getCreationTime().isAfter(time)).collect(toList());
    }

}
