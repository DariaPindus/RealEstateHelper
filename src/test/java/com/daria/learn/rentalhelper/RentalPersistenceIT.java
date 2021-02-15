package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.*;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;
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

    @Test
    @Order(1)
    public void testNewOffersAreSaved() {
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                new BriefRentalOfferDTO(repeatingName, "3030 AB", 50, "My Agency", "/link/to/1", 888, SOURCE),
                new BriefRentalOfferDTO("Apartment on 6th St", "3031 AB", 50, "My Agency", "/link/to/2", 999, SOURCE),
                new BriefRentalOfferDTO("Apartment on 7th St", "3031 AB", 50, "Another Agency",  "/link/to/3", 1111, SOURCE));
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        List<RentalOffer> persistedOffers = (List<RentalOffer>)rentalOfferRepository.findAll();
        assertEquals(newRentalOffers.size(), persistedOffers.size());

        List<OfferHistory> offerHistories1 = rentalOfferRepository.findByIdWithOfferHistories(persistedOffers.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<OfferHistory> offerHistories2 = rentalOfferRepository.findByIdWithOfferHistories(persistedOffers.get(1).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        List<OfferHistory> offerHistories3 = rentalOfferRepository.findByIdWithOfferHistories(persistedOffers.get(2).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, offerHistories1.size());
        assertEquals(1, offerHistories2.size());
        assertEquals(1, offerHistories3.size());
    }

    @Test
    @Order(2)
    public void testNewOfferAmongExistingIsSaved() {
        BriefRentalOfferDTO newOffer = new BriefRentalOfferDTO("Apartment on 8th St", "3032 AB", 50, "Another Agency",  "/link/to/3", 1100, SOURCE);
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                new BriefRentalOfferDTO(repeatingName, "3030 AB", 50, "My Agency",  "/link/to/1", 1000, SOURCE),
                newOffer);
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        List<RentalOffer> resaved = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(repeatingName)).collect(Collectors.toList());
        assertEquals(1, resaved.size());

        List<OfferHistory> resavedOfferHistories = rentalOfferRepository.findByIdWithOfferHistories(resaved.get(0).getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, resavedOfferHistories.size());

        List<RentalOffer> newPersisted = rentalOfferRepository.findBySearchStringIn(List.of(RentalOffer.generateSearchString(newOffer.getPostalCode(), newOffer.getArea(), newOffer.getAgency())));
        assertFalse(newPersisted.isEmpty());
        assertEquals(1, newPersisted.size());
    }

    @Test
    @Order(3)
    public void testExistingOfferIsUpdated() {
        String postalCode = "3033 AB";
        String link= "/link/to/3";
        int area = 50;
        String agency = "Another Agency";
        String consistentOfferName = "Apartment on 9th St";

        String updatedName = "Apartment on 7th St";
        double updatedPrice = 1500.0;

        BriefRentalOfferDTO newOffer1 = new BriefRentalOfferDTO("Apartment on 8th St", postalCode, area, agency, link, 1000, SOURCE);
        BriefRentalOfferDTO newOffer2 = new BriefRentalOfferDTO("Apartment on 9th St", "3003 bb", 60, agency, "/link/to/4", 888, SOURCE);
        RentalOfferDetailsDTO offerDetailsToBeUpdated = new RentalOfferDetailsDTO("Apartment on 8th St", link, RentalStatus.AVAILABLE, postalCode,
                1000.0, false, Instant.now(), false, area, agency );
        RentalOfferDetailsDTO offerDetailsToBeTheSame = new RentalOfferDetailsDTO(consistentOfferName, "/link/to/4", RentalStatus.AVAILABLE, "3003 bb",
                888.0, null, Instant.now(), false, 60, agency );
        rentalPersistenceFacade.persistNewRentals(List.of(newOffer1, newOffer2));
        rentalPersistenceFacade.updateRentalDetails(List.of(offerDetailsToBeUpdated, offerDetailsToBeTheSame));

        RentalOfferDetailsDTO updatedOfferDTO = new RentalOfferDetailsDTO(updatedName, link, RentalStatus.AVAILABLE, postalCode,
                updatedPrice, false, Instant.now(), false, area, agency );
        RentalOfferDetailsDTO offerDetailsTheSame = new RentalOfferDetailsDTO(consistentOfferName, "/link/to/4", RentalStatus.AVAILABLE, "3003 bb",
                888.0, null, Instant.now(), false, 60, agency);
        rentalPersistenceFacade.updateRentalDetails(List.of(updatedOfferDTO, offerDetailsTheSame));

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        RentalOffer updatedOffer = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(updatedName)).findFirst().get();
        assertEquals(updatedPrice, updatedOffer.getPrice(), 0.001);
        assertEquals(postalCode, updatedOffer.getPostalCode());
        assertEquals(link, updatedOffer.getLink());

        List<OfferHistory> offerHistories = rentalOfferRepository.findByIdWithOfferHistories(updatedOffer.getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(2, offerHistories.size());
        assertTrue(offerHistories.stream().anyMatch(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.PRICE_FIELD)));
        assertTrue(offerHistories.stream().anyMatch(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.NAME_FIELD)));

        RentalOffer consistentOffer = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(consistentOfferName)).findFirst().get();
        List<OfferHistory> consistentOfferHistories = rentalOfferRepository.findByIdWithOfferHistories(consistentOffer.getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, consistentOfferHistories.size());
    }

    @Test
    @Order(4)
    public void testExistingOfferIsDeleted() {
        //TODO
    }

    @Test
    public void testMultipleUpdates() {

    }
}
