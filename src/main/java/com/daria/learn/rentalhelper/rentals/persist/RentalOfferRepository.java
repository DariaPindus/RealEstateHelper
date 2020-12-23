package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.FieldHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.Pageable;

import javax.persistence.Tuple;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RentalOfferRepository {
    String EXCEPTION_MESSAGE_TEMPLATE ="%s is not supported by %s";

    /**
     * @param searchStrings list of search strings generated via {@link RentalOffer#generateSearchString}
     * @return {@link RentalOffer} list with search string existing in searchStrings
     */
    List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings);

    /**
     * @param name - {@link RentalOffer#name} to find
     * @return First {@link RentalOffer} with name and fetched {@link RentalOffer#offerHistories} if found
     */
    Optional<RentalOffer> findOfferHistoriesByOfferName(String name);

    /**
     * @param subname - partially matching {@link RentalOffer#name} to find
     * @return list of {@link RentalOffer} with names containing subname ignore case
     */
    List<RentalOffer> findAllByNameContains(String subname);

    /**
     * @param agency {@link RentalOffer#agency} to find
     * @param pageable - contains info about pageSize and offset of results
     * @return list of {@link RentalOffer} with size no more than @param pageable.getPageSize() elements
     */
    List<RentalOffer> findAllByAgencyPaged(String agency, Pageable pageable);

    List<RentalOffer> findAllSortedByPriceAscPaged(Pageable pageable);

    List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area);

    /**
     * @param time - time after which records should be updated
     * @return list of {@link RentalOffer} with {@link RentalOffer#offerHistories} where offerHistories are updated
     * (have {@link OfferHistory#status} == {@link OfferStatus#UPDATED})
     * after given time
     */
    List<RentalOffer> findAllWithHistoryUpdatedAfter(Instant time);

    /**
     * @param time - time after which records should be updated
     * @return list of {@link ImmutablePair} with {@link RentalOffer} and {@link OfferHistory} where offerHistories are created, updated, removed (all {@link OfferStatus#values()}
     * and list is sorted by {@link OfferHistory#time} desc to get latest modified offers
     */
    List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time);

    List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000();

    long countAll();

    long countCreatedInLastMonth();

    void saveList(List<RentalOffer> offers);

    default UnsupportedOperationException unsupportedException(String method, String implementation) {
        return new UnsupportedOperationException(String.format(EXCEPTION_MESSAGE_TEMPLATE, method, implementation));
    }

    String getName();

    //TODO: add dynamic query: i.e. preference

    //TODO: add aggregation: i.e. count offers by agency (group by agency)
    Optional<ImmutablePair<String, Long>> getAgencyWithMostOffersLast30Days();
}
