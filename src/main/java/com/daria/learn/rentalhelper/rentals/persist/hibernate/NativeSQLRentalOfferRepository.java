package com.daria.learn.rentalhelper.rentals.persist.hibernate;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//https://www.devglan.com/spring-boot/spring-boot-hibernate-5-example
@Repository
public class NativeSQLRentalOfferRepository implements RentalOfferRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RentalOffer> findBySearchStringIn(Collection<String> searchStrings) {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery(
                "SELECT * FROM RentalOffer ro where ro.searchString in :searchStrings", RentalOffer.class )
                .setParameter("searchStrings", searchStrings)
                .getResultList();
        return rentalOffers;
    }

    @Override
    public Optional<RentalOffer> findOfferHistoriesByOfferName(String name) {
//        List<Object[]> tuples = entityManager.unwrap(Session.class).createNativeQuery(
//                "SELECT * " +
//                        "FROM RentalOffer offer " +
//                        "JOIN OfferHistory oh ON oh.rentalOffer_id = offer.id where offer.name=:name" )
//                .setParameter("name", name)
//                .addEntity("offer", RentalOffer.class )
//                .addJoin( "oh", "offer.offerHistories")
//                .list();
        List<RentalOffer> tuples = entityManager.createNativeQuery(
                    "SELECT ro.* FROM RentalOffer ro join OfferHistory oh on oh.rentalOffer_id=ro.id where ro.name=:name limit 1",
                    RentalOffer.class )
                .setParameter("name", name).getResultList();
        if (tuples.isEmpty() ) return Optional.empty();
        RentalOffer rentalOffer = tuples.get(0);
        List<OfferHistory> histories = entityManager.createNativeQuery("select oh.* from OfferHistory oh where oh.rentalOffer_id=:offer_id", OfferHistory.class)
                .setParameter("offer_id", rentalOffer.getId())
                .getResultList();
        rentalOffer.setOfferHistories(histories);
        return Optional.of(rentalOffer);
    }

    @Override
    public List<RentalOffer> findAllByNameContains(String subname) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM RentalOffer ro where ro.name like :subname", RentalOffer.class )
                .setParameter("subname", String.format("%%%s%%", subname));
        return query.getResultList();
    }

    @Override
    public List<RentalOffer> findAllByAgencyPaged(String agency, Pageable pageable) {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery(
                "SELECT * FROM RentalOffer ro where ro.agency=:agency limit :offset, :limit", RentalOffer.class )
                .setParameter("agency", agency)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .getResultList();
        return rentalOffers;
    }

    @Override
    public List<RentalOffer> findAllSortedByPriceAscPaged(Pageable pageable) {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery(
                "SELECT * FROM RentalOffer ro order by ro.price asc limit :offset, :limit", RentalOffer.class )
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .getResultList();
        return rentalOffers;
    }

    @Override
    public List<RentalOffer> findAllByPriceGreaterThanAndAreaLessThan(double price, int area) {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery(
                "SELECT * FROM RentalOffer ro where ro.price >= :price and ro.area <= :area", RentalOffer.class )
                .setParameter("price", price)
                .setParameter("area", area)
                .getResultList();
        return rentalOffers;
    }

    @Override
    public List<RentalOffer> findAllWithHistoryUpdatedAfter(Instant time) {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery(
                "SELECT ro.*, oh.* FROM RentalOffer ro join OfferHistory oh on oh.rentalOffer_id=ro.id where oh.time>=:time", RentalOffer.class )
                .setParameter("time", time)
                .getResultList();
        return rentalOffers;
    }

    @Override
    @Transactional
    public List<ImmutablePair<RentalOffer, OfferHistory>> findAllModifiedAfterSortedByTimeDesc(Instant time) {
        Session session = entityManager.unwrap( Session.class );
        List<Object[]> resultTuples = session.createNativeQuery(
                "SELECT {ro.*}, {oh.*} FROM RentalOffer ro join OfferHistory oh on oh.rentalOffer_id=ro.id where oh.time>=:time order by oh.time desc")
                .setParameter("time", time)
                .addEntity("ro", RentalOffer.class)
                .addEntity("oh", OfferHistory.class)
                .getResultList();
        return resultTuples.stream().map(objects -> new ImmutablePair<>((RentalOffer)objects[0], (OfferHistory)objects[1])).collect(Collectors.toList());
    }

    @Override
    public List<RentalOffer> findAllPriceGrewUpInLast2WeeksLimit5000() {
        List<RentalOffer> rentalOffers = entityManager.createNativeQuery("select * from RentalOffer ro join " +
                "(select oh.rentalOffer_id as offer_id, sum(oh.delta) as sm from OfferHistory oh " +
                "where oh.fieldName=\"price\" and oh.time >= date_sub(now(), interval 14 day) group by oh.RentalOffer_id having sm > 0) as sub " +
                "on ro.id=sub.offer_id limit 5000", RentalOffer.class)
                .getResultList();
        return rentalOffers;
    }

    @Override
    public long countAll() {
        Query query = entityManager.createNativeQuery("select count(ro.id) from RentalOffer ro");
        return ((BigInteger)query.getSingleResult()).longValue();
    }

    @Override
    public long countCreatedInLastMonth() {
        Query query = entityManager.createNativeQuery("select count(distinct oh.rentalOffer_id) from OfferHistory oh " +
                "WHERE oh.status=\"NEW\" and oh.time >=date_sub(now(), interval 30 day);");
        return ((BigInteger)query.getSingleResult()).longValue();
    }

    @Override
    @Transactional
    public void saveList(List<RentalOffer> offers) {
        offers.forEach(entityManager::persist);
    }

    @Override
    public String getName() {
        return "NativeEntitySQLRentalOfferRepository";
    }

    @Override
    public Optional<ImmutablePair<String, Long>> getAgencyWithMostOffersLast30Days() {
        List<Object[]> results = entityManager.createNativeQuery("select ro.agency, count(oh.id) as cnt from OfferHistory oh join RentalOffer ro on oh.rentalOffer_id=ro.id " +
                "where oh.time >= date_sub(now(), interval 30 day) group by ro.agency order by cnt desc limit 1").getResultList();
        if (results.isEmpty())
            return Optional.empty();
        Object[] result = results.get(0);
        BigInteger count = (BigInteger)result[1];
        return Optional.of(new ImmutablePair<>((String)result[0], count.longValue()));
    }
}
