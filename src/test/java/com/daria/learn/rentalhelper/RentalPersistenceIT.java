package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.FieldHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RentalNotifierIT
public class RentalPersistenceIT extends DatabaseITBase {

    @Autowired
    RentalPersistenceFacade rentalPersistenceFacade;

    @Autowired
    RentalOfferRepository rentalOfferRepository;

    private final String repeatingName = "Apartment on 5th St";

    @Test
    @Order(1)
    public void testNewOffersAreSaved() {
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName,"3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                new RentalOfferDTO("Apartment on 6th St", "3031 AB", 1200, 50, "My Agency", true, "/link/to/2"),
                new RentalOfferDTO("Apartment on 7th St", "3031 AB", 1200, 50, "Another Agency", false, "/link/to/3"));
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        List<RentalOffer> persistedOffers = (List<RentalOffer>)rentalOfferRepository.findAll();
        assertEquals(newRentalOffers.size(), persistedOffers.size());

        List<FieldHistory> offerHistories1 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<FieldHistory> offerHistories2 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(1).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<FieldHistory> offerHistories3 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(2).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, offerHistories1.size());
        assertEquals(1, offerHistories2.size());
        assertEquals(1, offerHistories3.size());
    }

    @Test
    @Order(2)
    public void testNewOfferAmongExistingIsSaved() {
        RentalOfferDTO newOffer = new RentalOfferDTO("Apartment on 8th St", "3032 AB", 1200, 50, "Another Agency", false, "/link/to/3");
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> resaved = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(repeatingName)).collect(Collectors.toList());
        assertEquals(1, resaved.size());

        List<FieldHistory> resavedOfferHistories = rentalOfferRepository.findOfferHistoryById(resaved.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, resavedOfferHistories.size());

        List<RentalOffer> newPersisted = rentalOfferRepository.findBySearchStringIn(List.of(RentalOffer.generateSearchString(newOffer.getPostalCode(), newOffer.getArea(), newOffer.getAgency())));
        assertFalse(newPersisted.isEmpty());
        assertEquals(1, newPersisted.size());
    }

    @Test
    @Order(3)
    public void testExistingOfferIsUpdated() {
        String updatedName = "Apartment on 7th St";
        int updatedPrice = 1500;
        RentalOfferDTO newOffer = new RentalOfferDTO("Apartment on 8th St", "3033 AB", 1200, 50, "Another Agency", false, "/link/to/3");
        RentalOfferDTO updatedOffer = new RentalOfferDTO(updatedName, "3031 AB", updatedPrice, 50, "Another Agency", false, "/link/to/3");
        List<RentalOfferDTO> newRentalOffers = List.of(
                new RentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer,
                updatedOffer);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> updated = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(updatedName)).collect(Collectors.toList());
        assertEquals(1, updated.size());

        List<FieldHistory> offerHistories = rentalOfferRepository.findOfferHistoryById(updated.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(2, offerHistories.size());
        assertTrue(offerHistories.stream().anyMatch(fieldHistory -> fieldHistory.getStatus() == OfferStatus.UPDATED && fieldHistory.getFieldHistory().getFieldName().equals("price")));
    }

    @Test
    @Order(4)
    public void testExistingOfferIsDeleted() {
        //TODO
    }

}
