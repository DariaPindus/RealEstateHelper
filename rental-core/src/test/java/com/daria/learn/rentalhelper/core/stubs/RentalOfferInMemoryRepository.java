package com.daria.learn.rentalhelper.core.stubs;

import com.daria.learn.rentalhelper.core.domain.RentalOffer;
import com.daria.learn.rentalhelper.core.domain.RentalStatus;
import com.daria.learn.rentalhelper.core.persist.RentalOfferRepository;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class RentalOfferInMemoryRepository extends InMemoryRepository<RentalOffer, Integer> implements RentalOfferRepository {

    @Override
    public List<RentalOffer> findBySource(String source) {
        return storage.values().stream().filter(rentalOffer -> rentalOffer.getRentalStatus() == RentalStatus.AVAILABLE).collect(toList());
    }

    @Override
    public List<RentalOffer> findByLinkIn(Collection<String> searchLinks) {
        Set<String> linkSet = new HashSet<>(searchLinks);
        return storage.values().stream().filter(rentalOffer -> searchLinks.contains(rentalOffer.getSearchString())).collect(toList());
    }

    @Override
    public Optional<RentalOffer> findByIdWithOfferHistories(Integer id) {
        return findById(id);
    }

}
