package com.daria.learn.rentalhelper.rentals.persist.jpa;

import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.springframework.data.domain.Pageable;
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
    public Optional<RentalOffer> findOfferHistoryByName(String name) {
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
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        return jpaMethodRentalOfferRepository.findAllByPriceGreaterThanAndAreaLessThan(price, area);
    }

    @Override
    public List<RentalOffer> findAllUpdatedAfter(Instant time) {
        return jpaMethodRentalOfferRepository.findByOfferHistories_TimeGreaterThan(time);
    }

    @Override
    public List<RentalOffer> findThousandUpdatedByFieldName(String fieldName) {
        return jpaMethodRentalOfferRepository.findFirst1000ByOfferHistories_FieldHistoryFieldNameIs(fieldName);
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLastWeek() {
        throw unsupportedException("findAllPriceGrewUp", IMPLEMENTATION_NAME);
    }

    @Override
    public long countAll() {
        return jpaMethodRentalOfferRepository.count();
    }

    @Override
    public long countCreatedInLastMonth() {
        return jpaMethodRentalOfferRepository.countByOfferHistories_TimeGreaterThanAndOfferHistories_StatusIs(Instant.now().minus(1, ChronoUnit.MONTHS), OfferStatus.NEW);
    }

    @Override
    public void saveList(List<RentalOffer> offers) {
        jpaMethodRentalOfferRepository.saveAll(offers);
    }

}
