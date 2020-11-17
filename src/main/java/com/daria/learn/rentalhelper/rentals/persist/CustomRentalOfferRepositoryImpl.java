package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Optional;

@Repository
public class CustomRentalOfferRepositoryImpl implements CustomRentalOfferRepository {

    private final SessionFactory sessionFactory;

    public CustomRentalOfferRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

//    @Override
    @Deprecated
    public Optional<RentalOffer> findExistingOffer(RentalOffer offer) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("RentalOffer_FindBySearchableFields", RentalOffer.class)
                .setParameter("address", offer.getName())
                .setParameter("agency", offer.getAgency())
                .setParameter("postalCode", offer.getPostalCode());
        try {
            Object existingOffer = query.getSingleResult();
            return Optional.of((RentalOffer)existingOffer);
        } catch (NoResultException noResultException) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RentalOffer> findOfferHistoryById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ro from RentalOffer ro join fetch ro.offerHistories oh where ro.id=:offerId", RentalOffer.class)
                .setParameter("offerId", id);
        try {
            Object existingOffer = query.getSingleResult();
            return Optional.of((RentalOffer)existingOffer);
        } catch (NoResultException noResultException) {
            return Optional.empty();
        }
    }
}
