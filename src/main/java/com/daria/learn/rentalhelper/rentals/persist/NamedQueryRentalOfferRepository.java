package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpaRentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
//@Profile(ApplicationProfiles.NAMED_QUERIES_PROFILE)
/*
 *  By default, Spring Data JPA checks for a named JPQL or a named native query
 *  that follows the naming convention <entity class name>.<repository method name> before it tries to derive a query from the method name.
 */
public class NamedQueryRentalOfferRepository implements RentalOfferRepository {

    @Autowired
    private JpaRentalOfferRepository rentalOfferRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        Query q = entityManager.createNamedQuery("rentalOffer_findBySearchStringIn");
        q.setParameter(1, searchStrings);
        return q.getResultList();
    }

    @Override
    public Optional<RentalOffer> findOfferHistoriesByOfferName(String name) {
        try {
            Query q = entityManager.createNamedQuery("rentalOffer_findByName");
            q.setParameter(1, name);
            return Optional.of((RentalOffer)q.getSingleResult());
        } catch (NoResultException | NonUniqueResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<RentalOffer> findAllByNameContains(String subname) {
        Query q = entityManager.createNamedQuery("rentalOffer_findByNameContains");
        q.setParameter(1, subname);
        return q.getResultList();
    }

    @Override
    public List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        Query q = entityManager.createNamedQuery("rentalOffer_findAllByAgencyPaged");
        q.setParameter(1, name);
        q.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
        q.setMaxResults(pageable.getPageSize());
        return q.getResultList();
    }

    @Override
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        Query q = entityManager.createNamedQuery("rentalOffer_findAllByPriceGreaterThanAndAreaLessThan");
        q.setParameter(1, price);
        q.setParameter(2, area);
        return q.getResultList();
    }

    @Override
    public List<RentalOffer> findAllUpdatedAfter(Instant time) {
        Query q = entityManager.createNamedQuery("rentalOffer_findAllUpdatedAfter");
        q.setParameter(1, time);
        q.setParameter(2, OfferStatus.UPDATED);
        return q.getResultList();
    }

    @Override
    public List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time) {
        Query q = entityManager.createNamedQuery("rentalOffer_findAllUpdatedAfterOrdered");
        q.setParameter(1, time);
        return q.getResultList();
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000() {
        Query q = entityManager.createNamedQuery("rentalOffer_findAllPriceGrewUp");
        q.setParameter(1, Instant.now().minus(1, ChronoUnit.WEEKS));
        return q.getResultList();
    }

    @Override
    public long countAll() {
        Query q= entityManager.createNamedQuery("rentalOffer_countAll");
        return ((Number)q.getSingleResult()).intValue();
    }

    @Override
    public long countCreatedInLastMonth() {
        Query q= entityManager.createNamedQuery("rentalOffer_countCreatedInLastMonth");
        q.setParameter(1, Instant.now().minus(30, ChronoUnit.DAYS));
        q.setParameter(2, OfferStatus.NEW);
        return ((Number)q.getSingleResult()).intValue();
    }

    @Override
    public void saveList(List<RentalOffer> offers) {
        rentalOfferRepository.saveAll(offers);
    }

    @Override
    public String getName() {
        return "namedQuery";
    }
}
