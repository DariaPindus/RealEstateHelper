package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
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
    private final String SOURCE = "pararius";

    /*
    @Test
    @Order(1)
    public void testNewOffersAreSaved() {
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                new BriefRentalOfferDTO(repeatingName, "3030 AB", 50, "My Agency", "/link/to/1", SOURCE),
                new BriefRentalOfferDTO("Apartment on 6th St", "3031 AB", 50, "My Agency", "/link/to/2", SOURCE),
                new BriefRentalOfferDTO("Apartment on 7th St", "3031 AB", 50, "Another Agency",  "/link/to/3", SOURCE));
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        List<RentalOffer> persistedOffers = (List<RentalOffer>)rentalOfferRepository.findAll();
        assertEquals(newRentalOffers.size(), persistedOffers.size());

        List<OfferHistory> offerHistories1 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<OfferHistory> offerHistories2 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(1).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<OfferHistory> offerHistories3 = rentalOfferRepository.findOfferHistoryById(persistedOffers.get(2).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, offerHistories1.size());
        assertEquals(1, offerHistories2.size());
        assertEquals(1, offerHistories3.size());
    }

    @Test
    @Order(2)
    public void testNewOfferAmongExistingIsSaved() {
        BriefRentalOfferDTO newOffer = new BriefRentalOfferDTO("Apartment on 8th St", "3032 AB", 1200, 50, "Another Agency", false, "/link/to/3");
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                new BriefRentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> resaved = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(repeatingName)).collect(Collectors.toList());
        assertEquals(1, resaved.size());

        List<OfferHistory> resavedOfferHistories = rentalOfferRepository.findOfferHistoryById(resaved.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
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
        BriefRentalOfferDTO newOffer = new BriefRentalOfferDTO("Apartment on 8th St", "3033 AB", 50, "Another Agency", "/link/to/3", SOURCE);
        BriefRentalOfferDTO updatedOffer = new BriefRentalOfferDTO(updatedName, "3031 AB", updatedPrice, 50, "Another Agency", "/link/to/3");
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                new BriefRentalOfferDTO(repeatingName, "3030 AB", 1200, 50, "My Agency", true, "/link/to/1"),
                newOffer,
                updatedOffer);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> updated = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(updatedName)).collect(Collectors.toList());
        assertEquals(1, updated.size());

        List<OfferHistory> offerHistories = rentalOfferRepository.findOfferHistoryById(updated.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(2, offerHistories.size());
        assertTrue(offerHistories.stream().anyMatch(fieldHistory -> fieldHistory.getStatus() == OfferStatus.UPDATED && fieldHistory.getFieldHistory().getFieldName().equals("price")));
    }

    @Test
    @Order(4)
    public void testExistingOfferIsDeleted() {
        //TODO
    }

    @Test
    public void testMultipleUpdates() {
        BriefRentalOfferDTO firstOfferDTO = new BriefRentalOfferDTO("Apartment 11", "1234 AB", 1200, 50, "Another Agency", false, "/link/to/11");
        BriefRentalOfferDTO secondOfferDTO = new BriefRentalOfferDTO("Apartment 22", "3214 BB", 1000, 50, "My Agency", true, "/link/to/22");
        List<BriefRentalOfferDTO> newRentalOffers = List.of(firstOfferDTO, secondOfferDTO);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        BriefRentalOfferDTO firstOfferDTO2 = new BriefRentalOfferDTO("Apartment 11", "1234 AB", 1500, 50, "Another Agency", false, "/link/to/11");
        List<BriefRentalOfferDTO> changedOffers = rentalPersistenceFacade.persistNewRentals(List.of(firstOfferDTO2));

        assertEquals(1, changedOffers.size());
        BriefRentalOfferDTO changed = changedOffers.get(0);
        assertEquals("Apartment 11", changed.getName());
        assertEquals(1500, changed.getPrice(), 0.001);

        BriefRentalOfferDTO firstOfferDTO3 = new BriefRentalOfferDTO("Apartment 11", "1234 AB", 1200, 50, "Another Agency", false, "/link/to/11");
        List<BriefRentalOfferDTO> changedOffers2 = rentalPersistenceFacade.persistNewRentals(List.of(firstOfferDTO3));

        assertEquals(1, changedOffers2.size());
        BriefRentalOfferDTO changed2 = changedOffers2.get(0);
        assertEquals("Apartment 11", changed2.getName());
        assertEquals(1200, changed2.getPrice(), 0.001);

        BriefRentalOfferDTO firstOfferDTO4 = new BriefRentalOfferDTO("Apartment 11", "1234 AB", 1500, 50, "Another Agency", false, "/link/to/11");
        List<BriefRentalOfferDTO> changedOffers3 = rentalPersistenceFacade.persistNewRentals(List.of(firstOfferDTO4));

        assertEquals(1, changedOffers3.size());
        BriefRentalOfferDTO changed3 = changedOffers3.get(0);
        assertEquals("Apartment 11", changed3.getName());
        assertEquals(1500, changed3.getPrice(), 0.001);
    }
     */
}
