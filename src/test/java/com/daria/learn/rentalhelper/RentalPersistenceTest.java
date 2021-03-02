package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.*;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.daria.learn.rentalhelper.DomainHelper.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RentalNotifierTest
public class RentalPersistenceTest {

    @Autowired
    RentalPersistenceFacade rentalPersistenceFacade;

    @Autowired
    RentalOfferRepository rentalOfferRepository;

    private final String SOURCE = "pararius";

    @Test
    public void testNewOffersAreSaved() {
        List<String> names = List.of("Apartment 1", "Apartment 2", "Apartment 3");
        List<BriefRentalOfferDTO> newRentalOffers = List.of(
                createBriefRentalOfferDTO(names.get(0), SOURCE),
                createBriefRentalOfferDTO(names.get(1), SOURCE),
                createBriefRentalOfferDTO(names.get(2), SOURCE));
        rentalPersistenceFacade.persistNewRentals(newRentalOffers);

        List<RentalOffer> persistedOffers = StreamSupport.stream(rentalOfferRepository.findAll().spliterator(), false)
                .filter(rentalOffer -> names.contains(rentalOffer.getName())).collect(toList());
        assertEquals(newRentalOffers.size(), persistedOffers.size());

        String link = newRentalOffers.get(0).getLink();
        RentalOffer persistedOffer = persistedOffers.stream()
                .filter(offer -> offer.getLink().equals(link))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rental offer with link " + link + " wasn't saved."));
        assertTrue(isSameRentalOffer(newRentalOffers.get(0), persistedOffer));

        List<OfferHistory> offerHistories1 = rentalOfferRepository.findByIdWithOfferHistories(persistedOffers.get(0).getId())
                .map(RentalOffer::getOfferHistories)
                .orElseThrow(() -> new RuntimeException("Expected rental offer " + persistedOffers.get(0).getId() + " wasn't found"));
        assertEquals(0, offerHistories1.size());
    }

    @Test
    public void testNewOfferAmongExistingIsSaved() {
        BriefRentalOfferDTO newOffer = createBriefRentalOfferDTO("Apartment 4", SOURCE);
        BriefRentalOfferDTO newOffer2 = createBriefRentalOfferDTO("Apartment 5", SOURCE);
        rentalPersistenceFacade.persistNewRentals(List.of(newOffer, newOffer2));

        List<RentalOffer> persistedOffers =  StreamSupport.stream(rentalOfferRepository.findAll().spliterator(), false)
                .filter(rentalOffer -> rentalOffer.getSearchString().equals(RentalOffer.generateSearchStringFromDTO(newOffer)) ||
                        rentalOffer.getSearchString().equals(RentalOffer.generateSearchStringFromDTO(newOffer2)) )
                .collect(toList());
        assertEquals(2, persistedOffers.size());

        BriefRentalOfferDTO fromOffer1 = copyBriefRentalOfferDTO(newOffer, "Apartment 4 updated");
        BriefRentalOfferDTO newOffer3 = createBriefRentalOfferDTO("Apartment 5", SOURCE);
        rentalPersistenceFacade.persistNewRentals(List.of(fromOffer1, newOffer3));

        persistedOffers = StreamSupport.stream(rentalOfferRepository.findAll().spliterator(), false)
                .filter(rentalOffer -> rentalOffer.getSearchString().equals(RentalOffer.generateSearchStringFromDTO(newOffer)) ||
                        rentalOffer.getSearchString().equals(RentalOffer.generateSearchStringFromDTO(newOffer2)) ||
                        rentalOffer.getSearchString().equals(RentalOffer.generateSearchStringFromDTO(newOffer3))
                )
                .collect(toList());
        assertEquals(3, persistedOffers.size());
    }

    @Test
    public void testExistingOfferIsUpdated() {
        double updatedPrice = 1212;
        Instant updatedAvailability = Instant.now().plus(14, ChronoUnit.DAYS);

        BriefRentalOfferDTO newOffer1 = createBriefRentalOfferDTO("Apartment 6", SOURCE);
        BriefRentalOfferDTO newOffer2 = createBriefRentalOfferDTO("Apartment 7", SOURCE);
        rentalPersistenceFacade.persistNewRentals(List.of(newOffer1, newOffer2));

        double initialPrice = newOffer1.getPrice();

        RentalOfferDetailsDTO offerDetailsToBeUpdated = fromBriefRentalOfferDTO(newOffer1);
        RentalOfferDetailsDTO offerDetailsToBeTheSame = fromBriefRentalOfferDTO(newOffer2);
        rentalPersistenceFacade.updateRentalDetails(List.of(offerDetailsToBeUpdated, offerDetailsToBeTheSame));

        RentalOfferDetailsDTO updatedOfferDTO = fromBriefRentalOfferDTOUpdated(newOffer1, updatedPrice, updatedAvailability);
        RentalOfferDetailsDTO offerDetailsTheSame = fromBriefRentalOfferDTOUpdated(newOffer2, offerDetailsToBeTheSame.getPrice(), offerDetailsToBeTheSame.getAvailableFrom());
        rentalPersistenceFacade.updateRentalDetails(List.of(updatedOfferDTO, offerDetailsTheSame));

        Iterable<RentalOffer> allOffers = rentalOfferRepository.findAll();
        RentalOffer updatedOffer = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(newOffer1.getName())).findFirst().get();
        assertEquals(updatedPrice, updatedOffer.getPrice(), 0.001);
        assertEquals(updatedAvailability, updatedOffer.getAvailableFrom());

        List<OfferHistory> offerHistories = rentalOfferRepository.findByIdWithOfferHistories(updatedOffer.getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(3, offerHistories.size());
        assertTrue(offerHistories.stream().anyMatch(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.PRICE_FIELD)));
        assertTrue(offerHistories.stream().anyMatch(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.AVAILABLE_FROM_FIELD)));

        RentalOffer consistentOffer = StreamSupport.stream(allOffers.spliterator(), false).filter(offer -> offer.getName().equals(newOffer2.getName())).findFirst().get();
        List<OfferHistory> consistentOfferHistories = rentalOfferRepository.findByIdWithOfferHistories(consistentOffer.getId()).map(RentalOffer::getOfferHistories).orElse(List.of());
        assertEquals(1, consistentOfferHistories.size());

        RentalOfferDetailsDTO updatedAgainOfferDTO = fromBriefRentalOfferDTOUpdated(newOffer1, initialPrice, updatedAvailability);
        rentalPersistenceFacade.updateRentalDetails(List.of(updatedAgainOfferDTO));
        updatedOffer = rentalOfferRepository.findByIdWithOfferHistories(updatedOffer.getId()).get();
        offerHistories = updatedOffer.getOfferHistories();
        assertEquals(initialPrice, updatedOffer.getPrice(), 0.001);
        assertEquals(4, offerHistories.size());
        assertEquals(2, offerHistories.stream().filter(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.PRICE_FIELD)).count());
    }

    @Test
    public void testCorrectUpdatedOffersShouldBeNotifiedAbout() {
        BriefRentalOfferDTO newOffer1 = createBriefRentalOfferDTO("Apartment 8", SOURCE);
        BriefRentalOfferDTO newOffer2 = createBriefRentalOfferDTO("Apartment 9", SOURCE);
        BriefRentalOfferDTO newOffer3 = createBriefRentalOfferDTO("Apartment 10", SOURCE);
        rentalPersistenceFacade.persistNewRentals(List.of(newOffer1, newOffer2, newOffer3));

        RentalOfferDetailsDTO initialDetailsOffer1 = fromBriefRentalOfferDTO(newOffer1);
        RentalOfferDetailsDTO initialDetailsOffer2 = fromBriefRentalOfferDTO(newOffer2);
        RentalOfferDetailsDTO initialDetailsOffer3 = fromBriefRentalOfferDTO(newOffer3);
        rentalPersistenceFacade.updateRentalDetails(List.of(initialDetailsOffer1, initialDetailsOffer2, initialDetailsOffer3));

        RentalOfferDetailsDTO detailsNotToNotifyAbout = copyRentalOfferDetailsDTOUpdated(initialDetailsOffer1, true, "Apartment 8 new", RentalStatus.UNDER_OPTION, initialDetailsOffer1.getAvailableFrom(), initialDetailsOffer1.getPrice() );
        RentalOfferDetailsDTO detailsToNotifyAbout = copyRentalOfferDetailsDTOUpdated(initialDetailsOffer2, initialDetailsOffer2.getIsFurnished(), "Apartment 9 new", initialDetailsOffer2.getStatus(), Instant.now(), initialDetailsOffer2.getPrice() + 10);
        RentalOfferDetailsDTO detailsNoChanges = copyRentalOfferDetailsDTOUpdated(initialDetailsOffer3, initialDetailsOffer3.getIsFurnished(), initialDetailsOffer3.getName(), initialDetailsOffer3.getStatus(), initialDetailsOffer3.getAvailableFrom(), initialDetailsOffer3.getPrice());
        List<RentalOfferDetailsDTO> DTOsToNotifyAbout = rentalPersistenceFacade.updateRentalDetails(List.of(detailsToNotifyAbout, detailsNotToNotifyAbout, detailsNoChanges));

        assertEquals(1, DTOsToNotifyAbout.size());
        assertTrue(DTOsToNotifyAbout.stream().allMatch(dto -> dto.getLink().equals(detailsToNotifyAbout.getLink())));

        RentalOfferDetailsDTO toNotifyAbout = DTOsToNotifyAbout.get(0);
        assertEquals(detailsToNotifyAbout.getAvailableFrom(), toNotifyAbout.getAvailableFrom());
        assertEquals(detailsToNotifyAbout.getPrice(), toNotifyAbout.getPrice());
        assertEquals(detailsToNotifyAbout.getStatus(), toNotifyAbout.getStatus());
        assertEquals(detailsToNotifyAbout.getName(), toNotifyAbout.getName());
        assertEquals(detailsToNotifyAbout.getAgency(), toNotifyAbout.getAgency());
        assertEquals(detailsToNotifyAbout.getArea(), toNotifyAbout.getArea());
        assertEquals(detailsToNotifyAbout.getPostalCode(), toNotifyAbout.getPostalCode());
    }

    @Test
    public void testExistingOfferIsDeleted() {
        String link1 = "/link/to/1";
        String link2 = "/link/to/2";

        BriefRentalOfferDTO newRental = new BriefRentalOfferDTO("Apartment 11", "12321", 33, "agency",link1 , 1111, SOURCE );
        BriefRentalOfferDTO newRental2 = new BriefRentalOfferDTO("Apartment 12", "12324", 55, "agency", link2, 1000, SOURCE );
        rentalPersistenceFacade.persistNewRentals(List.of(newRental, newRental2));

        List<RentalOffer> offers = rentalOfferRepository.findByLinkIn(List.of(link1, link2));
        assertEquals(2, offers.size());
        assertTrue(offers.stream().allMatch(offer -> offer.getRentalStatus() == RentalStatus.AVAILABLE));

        RentalOfferDetailsDTO updatedOffer1 = fromBriefRentalOfferDTO(newRental);
        RentalOfferDetailsDTO updatedOffer2 = missedFromBriefRentalOfferDTO(newRental2);
        rentalPersistenceFacade.updateRentalDetails(List.of(updatedOffer1, updatedOffer2));

        offers = rentalOfferRepository.findByLinkIn(List.of(link1, link2));
        assertEquals(2, offers.size());
        RentalOffer deletedRental = offers.stream().filter(offer -> offer.getLink().equals(link2)).findFirst().get();
        assertSame(deletedRental.getRentalStatus(), RentalStatus.DELETED);
        assertEquals(1000, deletedRental.getPrice(), 0.001);
        assertEquals("Apartment 12", deletedRental.getName());
        assertEquals(55, deletedRental.getArea());

        List<OfferHistory> offerHistories = deletedRental.getOfferHistories();
        assertEquals(1, offerHistories.size());
        List<OfferHistory> deletedHistories = offerHistories.stream()
                .filter(offerHistory -> offerHistory.getFieldName().equals(RentalOfferFields.RENTAL_STATUS_FIELD))
                .collect(toList());
        assertEquals(1, deletedHistories.size());
        assertEquals(RentalStatus.DELETED.getValue(), deletedHistories.get(0).getNewValue());

        List<RentalOffer> openOffers = rentalOfferRepository.findOpenRentalOffers(SOURCE);
        assertTrue(openOffers.stream().noneMatch(offer -> offer.getId().equals(deletedRental.getId())));
    }

}
