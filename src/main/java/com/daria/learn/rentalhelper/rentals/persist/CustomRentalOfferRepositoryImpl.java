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

    @Override
    public Optional<RentalOffer> findExistingOffer(RentalOffer offer) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select ro from RentalOffer ro " +
                "where ro.name=:address and " +
                "ro.agency=:agency and " +
                "ro.postalCode=:postalCode")
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

}
