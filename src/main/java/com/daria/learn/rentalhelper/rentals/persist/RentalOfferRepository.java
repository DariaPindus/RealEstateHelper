package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RentalOfferRepository {
    String EXCEPTION_MESSAGE_TEMPLATE ="%s is not supported by %s";

    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    Optional<RentalOffer> findOfferHistoryByName(String name);

    List<RentalOffer> findAllByNameContains(String subname);

    List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable);

    List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area);

    List<RentalOffer> findAllUpdatedAfter(Instant time);

    List<RentalOffer> findAllUpdatedByFieldName(String fieldName);

    List<RentalOffer> findAllPriceGrewUpInLastMonth();

    long countAll();

    long countCreatedInLastMonth();

    void saveList(List<RentalOffer> offers);

    default UnsupportedOperationException unsupportedException(String method, String implementation) {
        return new UnsupportedOperationException(String.format(EXCEPTION_MESSAGE_TEMPLATE, method, implementation));
    }
}
