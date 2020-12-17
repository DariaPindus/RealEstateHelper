package com.daria.learn.rentalhelper.rentals.persist.hibernate;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CriteriaRentalOfferRepository implements RentalOfferRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        CriteriaQuery<RentalOffer> criteria = getWhereCriteria((builder, root) -> root.get("searchString").in(searchStrings));
        return entityManager.createQuery( criteria ).getResultList();
    }

    @Override
    public Optional<RentalOffer> findOfferHistoriesByOfferName(String name) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<RentalOffer> criteria = builder.createQuery( RentalOffer.class );
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        criteria.select( root );
        criteria.where(builder.equal(root.get("name"), name));
        root.fetch("offerHistories");

        try {
            return Optional.ofNullable(entityManager.createQuery(criteria).getSingleResult());
        } catch (NonUniqueResultException | NoResultException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<RentalOffer> findAllByNameContains(String subname) {
        CriteriaQuery<RentalOffer> criteria = getWhereCriteria((builder, root) -> builder.like(root.get("name"), "%" + subname + "%"));
        return entityManager.createQuery( criteria ).getResultList();
    }

    @Override
    public List<RentalOffer> findAllByAgencyPaged(String name, Pageable pageable) {
        CriteriaQuery<RentalOffer> criteria = getWhereCriteria((builder, root) -> builder.equal(root.get("agency"), name));
        return entityManager.createQuery( criteria )
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        CriteriaQuery<RentalOffer> criteria = getWhereCriteria((builder, root) ->
                builder.and(builder.greaterThanOrEqualTo(root.get("price"), price), builder.lessThanOrEqualTo(root.get("area"), area)));
        return entityManager.createQuery( criteria ).getResultList();
    }

    @Override
    public List<RentalOffer> findAllUpdatedAfter(Instant time) {
        CriteriaQuery<RentalOffer> criteria = getWhereCriteriaWithOfferHistories((builder, root, offerHistories) -> builder.and(
                builder.greaterThanOrEqualTo(offerHistories.get("time"), time),
                builder.equal(offerHistories.get("status"), OfferStatus.UPDATED)));
        return entityManager.createQuery(criteria).getResultList().stream()
                .peek(rentalOffer ->
                        rentalOffer.setOfferHistories(
                                rentalOffer.getOfferHistories().stream().filter(offerHistory -> offerHistory.getStatus() == OfferStatus.UPDATED).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
        Root<RentalOffer> root = criteria.from(RentalOffer.class);
        Join<RentalOffer, OfferHistory> offerHistories = root.join("offerHistories");
        criteria.multiselect(root , offerHistories);
        criteria.orderBy(builder.desc(offerHistories.get("time")));
        List<Tuple> resultList = entityManager.createQuery(criteria).getResultList();
        return resultList.stream().map(tuple -> new ImmutablePair<>((RentalOffer)tuple.get(0), (OfferHistory)tuple.get(1))).collect(Collectors.toList());
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000() {
        // select ro from RentalOffer where ro.id in
//        (select oh.rentalOffer_id as offer_id from OfferHistory oh
//        where oh.fieldName="price" and
//        oh.time >= DATE(NOW()) - INTERVAL 14 DAY
//        group by oh.RentalOffer_id
//        having sum(oh.delta) > 0)

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RentalOffer> criteria = builder.createQuery(RentalOffer.class);
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        Join<RentalOffer, OfferHistory> offerHistories = root.join("offerHistories");
        Subquery<Integer> selectIdFromSum = criteria.subquery(Integer.class);
        Root<OfferHistory> offerHistoryRoot = selectIdFromSum.from(OfferHistory.class);
        selectIdFromSum
                .select(offerHistoryRoot.get("rentalOffer"))
                .where(
                        builder.and(
                            builder.equal(offerHistoryRoot.get("fieldHistory").get("fieldName"), "price")),
                            builder.greaterThanOrEqualTo(offerHistoryRoot.get("time"), Instant.now().minus(14, ChronoUnit.DAYS)))
                .having(builder.greaterThan(builder.sum(offerHistoryRoot.get("fieldHistory").get("delta")), 0))
                .groupBy(offerHistoryRoot.get("rentalOffer"));
        criteria.select(root).where(root.get("id").in(selectIdFromSum)).distinct(true);
        return entityManager.createQuery(criteria).setMaxResults(5000).getResultList();
    }

    @Override
    public long countAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        criteria.select(builder.count(root));
        return entityManager.createQuery(criteria).getSingleResult();
    }

    @Override
    public long countCreatedInLastMonth() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        Join<RentalOffer, OfferHistory> offerHistories = root.join("offerHistories");
//        root.fetch("offerHistories");
        criteria.select(builder.countDistinct(root)).where(
                builder.and(
                        builder.equal(offerHistories.get("status"), OfferStatus.NEW),
                        builder.greaterThanOrEqualTo(offerHistories.get("time"), Instant.now().minus(30, ChronoUnit.DAYS))));
        return entityManager.createQuery(criteria).getSingleResult();
    }

    @Override
    //TODO: OK?
    @Transactional
    public void saveList(List<RentalOffer> offers) {
        for (RentalOffer rentalOffer: offers)
            entityManager.persist(rentalOffer);
    }

    @Override
    public String getName() {
        return "criteria-api";
    }

    private CriteriaQuery<RentalOffer> getWhereCriteria(ExpressionBuilder whereExpression) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RentalOffer> criteria = builder.createQuery( RentalOffer.class );
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        criteria.select( root );
        criteria.where(whereExpression.buildWhereClause(builder, root));
        return criteria;
    }

    private CriteriaQuery<RentalOffer> getWhereCriteriaWithOfferHistories(ExpressionWithOfferHistoryBuilder whereExpression) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RentalOffer> criteria = builder.createQuery( RentalOffer.class );
        Root<RentalOffer> root = criteria.from( RentalOffer.class );
        Join<RentalOffer, OfferHistory> offerHistories = root.join("offerHistories");
        root.fetch("offerHistories");
        criteria.select( root ).where(whereExpression.buildWhereClause(builder, root, offerHistories)).distinct(true);
        return criteria;
    }

    @FunctionalInterface
    private interface ExpressionBuilder {
        Expression<Boolean> buildWhereClause(CriteriaBuilder builder, Root<RentalOffer> root);
    }

    @FunctionalInterface
    private interface ExpressionWithOfferHistoryBuilder {
        Expression<Boolean> buildWhereClause(CriteriaBuilder builder, Root<RentalOffer> root, Join<RentalOffer, OfferHistory> offerHistories);
    }
}
