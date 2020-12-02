package com.daria.learn.rentalhelper.performance;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.FieldHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.NamedQueryRentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpaMethodRentalOfferRepositoryAdapter;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpaQueryRentalOfferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE})
public class SqlPerformanceTest {

    @Autowired
    private JpaMethodRentalOfferRepositoryAdapter jpaMethodRentalOfferRepository;
    @Autowired
    private JpaQueryRentalOfferRepository jpaQueryRentalOfferRepository;
    @Autowired
    private NamedQueryRentalOfferRepository namedQueryRentalOfferRepository;

    static String testAgency = "";
    static boolean isSetup = false;
    private static String testName = "Apartment My test 11";

    @Test
    public void testJpaMethod() {
        setup();

        RentalOffer testOffer = new RentalOffer(testName, "1212 AA", 1200.0, 21, "hello", false, "dsdsdas");
        List<OfferHistory> testHistory = List.of(new OfferHistory(Instant.now(), OfferStatus.NEW, null, testOffer),new OfferHistory(Instant.now(), OfferStatus.UPDATED, new FieldHistory("area", "51", "21"), testOffer));
        testOffer.setOfferHistories(testHistory);
        jpaMethodRentalOfferRepository.saveList(List.of(testOffer));

        List<ExecutionDetails> executionResults = new ArrayList<>();

        ExecutionDetails foundRentals = executeLogged("jpaMethod_findBySearchStringIn", () -> jpaMethodRentalOfferRepository.findBySearchStringIn(List.of(testOffer.getSearchString())));
        executionResults.add(foundRentals);
        List<RentalOffer> foundByName = (List<RentalOffer>)foundRentals.getResult();
        assertTrue(!foundByName.isEmpty() && foundByName.stream().anyMatch(rentalOffer -> rentalOffer.getName().equals(testName)));

        String containsStr = "My test 11";
        ExecutionDetails nameContainsDetails = executeLogged("jpaMethod_findAllByNameContains", () -> jpaMethodRentalOfferRepository.findAllByNameContains(containsStr));
        executionResults.add(nameContainsDetails);
        List<RentalOffer> foundByNameContains = (List<RentalOffer>)nameContainsDetails.getResult();
        assertTrue(!foundByNameContains.isEmpty() && foundByNameContains.stream().allMatch(rentalOffer -> rentalOffer.getName().contains(containsStr)));

        ExecutionDetails historyByNameDetails = executeLogged("jpaMethod_findOfferHistoryByName", () -> jpaMethodRentalOfferRepository.findOfferHistoryByName(testName));
        executionResults.add(historyByNameDetails);
        RentalOffer historyByName = ((Optional<RentalOffer>)historyByNameDetails.getResult()).get();
        assertEquals(testHistory.size(), historyByName.getOfferHistories().size());
        assertTrue(historyByName.getOfferHistories().stream().allMatch(history -> history.getStatus() == OfferStatus.NEW || history.getStatus() == OfferStatus.UPDATED));

        ExecutionDetails namePagedDetails = executeLogged("jpaMethod_findAllByAgencyPaged_1", () -> jpaMethodRentalOfferRepository.findAllByAgencyPaged(testAgency, PageRequest.of(0, 5)));
        executionResults.add(namePagedDetails);
        List<String> foundByNamePage1 = ((List<RentalOffer>)namePagedDetails.getResult()).stream().map(RentalOffer::getSearchString).collect(Collectors.toList());
        ExecutionDetails namePagedDetails2 = executeLogged("jpaMethod_findAllByAgencyPaged_2", () -> jpaMethodRentalOfferRepository.findAllByAgencyPaged(testAgency, PageRequest.of(2, 5)));
        executionResults.add(namePagedDetails);
        List<String> foundByNamePage2 =  ((List<RentalOffer>)namePagedDetails2.getResult()).stream().map(RentalOffer::getSearchString).collect(Collectors.toList());
        assertTrue(foundByNamePage1.stream().noneMatch(foundByNamePage2::contains));

        //add limitations
        double minPrice = 1900.0;
        int maxArea = 20;
        ExecutionDetails multipleFiltersDetails = executeLogged("jpaMethod_findAllByPriceGreaterThanAndAreaLessThan", () -> jpaMethodRentalOfferRepository.findAllByPriceGreaterThanAndAreaLessThan(minPrice, maxArea));
        executionResults.add(multipleFiltersDetails);
        List<RentalOffer> multipleFiltersOffers = (List<RentalOffer>)multipleFiltersDetails.getResult();
        assertTrue(multipleFiltersOffers.stream().allMatch(rentalOffer -> rentalOffer.getPrice() >= minPrice && rentalOffer.getArea() <= maxArea ));

        Instant timeToCheck = Instant.now().minus(3, ChronoUnit.DAYS);
        ExecutionDetails updatedAfterDetails = executeLogged("jpaMethod_findAllUpdatedAfter", () -> jpaMethodRentalOfferRepository.findAllUpdatedAfter(timeToCheck));
        executionResults.add(updatedAfterDetails);
        List<RentalOffer> updatedAfterOffers = (List<RentalOffer>)updatedAfterDetails.getResult();
        assertTrue(updatedAfterOffers.stream().map(RentalOffer::getOfferHistories).flatMap(Collection::stream).allMatch(offerHistory -> offerHistory.getTime().isAfter(timeToCheck)));

        ExecutionDetails updatedByFieldDetails = executeLogged("jpaMethod_findAllUpdatedByFieldName", () -> jpaMethodRentalOfferRepository.findThousandUpdatedByFieldName("area"));
        executionResults.add(updatedByFieldDetails);
        List<RentalOffer> rentalOffers = (List<RentalOffer>)updatedByFieldDetails.getResult();
        assertTrue(rentalOffers.stream().map(RentalOffer::getOfferHistories).flatMap(Collection::stream).allMatch(offerHistory -> offerHistory.getFieldHistory().getFieldName().equals("area"))); //could not initialize proxy - no Session

        assertThrows(UnsupportedOperationException.class,() -> executeLogged("jpaMethod_findAllPriceGrewUpInLastWeek", () -> jpaMethodRentalOfferRepository.findAllPriceGrewUpInLastWeek()));

        ExecutionDetails countAllDetails= executeLogged("jpaMethod_countAll", () -> jpaMethodRentalOfferRepository.countAll());
        executionResults.add(countAllDetails);
        long countAllResult = (long)countAllDetails.getResult();

        ExecutionDetails countCreatedLastMonthDetails = executeLogged("jpaMethod_countCreatedInLastMonth", () -> jpaMethodRentalOfferRepository.countCreatedInLastMonth());
        executionResults.add(countCreatedLastMonthDetails);
        long createdLastMonthResults = (long)countCreatedLastMonthDetails.getResult();

    }

    private void setup() {
        if (!isSetup) {
            List<String> agencies = jpaQueryRentalOfferRepository.findDistinctAgencies();
            testAgency = agencies.get(0);
            jpaQueryRentalOfferRepository.deleteAll(jpaQueryRentalOfferRepository.findAllByNameContains(testName));
        }
    }


    private ExecutionDetails executeLogged(String name, Supplier<Object> function) {
        long start = System.currentTimeMillis();
        Object results = function.get();
        long end = System.currentTimeMillis();
        return new ExecutionDetails(name, results, end - start);
    }


    private class ExecutionDetails {
        private final String functionName;
        private final Object result;
        private final long duration;

        public String getFunctionName() {
            return functionName;
        }

        public Object getResult() {
            return result;
        }

        public long getDuration() {
            return duration;
        }

        private ExecutionDetails(String functionName, Object result, long duration) {
            this.functionName = functionName;
            this.result = result;
            this.duration = duration;
        }
    }
}
