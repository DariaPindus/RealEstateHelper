package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class RentalPersistenceIT {

    @Autowired
    RentalPersistenceFacade rentalPersistenceFacade;

    @Autowired
    RentalOfferRepository rentalOfferRepository;

    private final String repeatingName = "Apartment on 5th St";

    @Test
    public void testNewOffersAreSaved() {
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName,"3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                new RentalOfferDTO("Apartment on 5th St", "3031 AB", 1200, 50, "My Agency", true, "/link/to/2"),
                new RentalOfferDTO("Apartment on 7th St", "3032 AB", 1200, 50, "Another Agency", false, "/link/to/3"));
        rentalPersistenceFacade.persistRentals(newRentalOffers);

        List<RentalOffer> persistedOffers = (List<RentalOffer>)rentalOfferRepository.findAll();
        assertEquals(newRentalOffers.size(), persistedOffers.size());
        assertEquals(1, persistedOffers.get(0).getOfferHistories().size());
        assertEquals(1, persistedOffers.get(1).getOfferHistories().size());
        assertEquals(1, persistedOffers.get(2).getOfferHistories().size());
    }

    @Test
    public void testNewOfferAmongExistingIsSaved() {
        RentalOfferDTO newOffer = new RentalOfferDTO("Apartment on 8th St", "3032 AB", 1200, 50, "Another Agency", false, "/link/to/3");
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer);
        rentalPersistenceFacade.persistRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> resaved = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(repeatingName)).collect(Collectors.toList());
        List<RentalOffer> newPersisted = rentalOfferRepository.findBySearchStringIn(List.of(RentalOffer.generateSearchString(newOffer.getPostalCode(), newOffer.getArea(), newOffer.getAgency())));

        assertEquals(1, resaved.size());
        assertEquals(1, resaved.get(0).getOfferHistories().size());

        assertFalse(newPersisted.isEmpty());
        assertEquals(1, newPersisted.size());
    }

    @Test
    public void testExistingOfferIsUpdated() {
        String updatedName = "Apartment on 7th St";
        RentalOfferDTO newOffer = new RentalOfferDTO("Apartment on 8th St", "3033 AB", 1200, 50, "Another Agency", false, "/link/to/3");
        RentalOfferDTO updatedOffer = new RentalOfferDTO(updatedName, "3032 AB", 1500, 50, "Another Agency", false, "/link/to/3");
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer,
                updatedOffer);
        rentalPersistenceFacade.persistRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> updated = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(updatedName)).collect(Collectors.toList());

        assertEquals(1, updated.size());

        List<OfferHistory> offerHistories = updated.get(0).getOfferHistories();
        assertEquals(2, offerHistories.size());
        assertTrue(offerHistories.stream().anyMatch(offerHistory -> offerHistory.getStatus() == OfferStatus.UPDATED && offerHistory.getFieldHistory().getFieldName().equals("price")));
    }

    @Test
    public void testExistingOfferIsDeleted() {
        //TODO
    }

}
