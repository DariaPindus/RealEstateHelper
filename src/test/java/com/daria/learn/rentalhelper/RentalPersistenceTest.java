package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacadeImpl;
import com.daria.learn.rentalhelper.stubs.RentalOfferInMemoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RentalPersistenceTest {

    private RentalOfferRepository rentalOfferRepository = new RentalOfferInMemoryRepository();
    private RentalPersistenceFacade rentalPersistenceFacade = new RentalPersistenceFacadeImpl(rentalOfferRepository);


    @Test
    public void testpersistNewRentals() {
    }
}
