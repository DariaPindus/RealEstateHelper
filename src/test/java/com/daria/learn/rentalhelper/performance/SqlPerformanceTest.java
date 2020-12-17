package com.daria.learn.rentalhelper.performance;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.FieldHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.NamedQueryRentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.OfferHistoryRepository;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.hibernate.CriteriaRentalOfferRepository;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpaMethodRentalOfferRepositoryAdapter;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpqlQueryRentalOfferRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE})
public class SqlPerformanceTest {

    @Autowired
    private OfferHistoryRepository offerHistoryRepository;
    @Autowired
    private JpaMethodRentalOfferRepositoryAdapter jpaMethodRentalOfferRepository;
    @Autowired
    private JpqlQueryRentalOfferRepository jpqlQueryRentalOfferRepository;
    @Autowired
    private NamedQueryRentalOfferRepository namedQueryRentalOfferRepository;
    @Autowired
    private CriteriaRentalOfferRepository criteriaRentalOfferRepository;

    static String testAgency = "";
    static boolean isSetup = false;
    private static String testName = "Apartment My test 11";

    @Test
    public void testJpaMethod() {
        executeTests(jpaMethodRentalOfferRepository, new OrmMethodConfig(Set.of("findAllModifiedAfterSortedByTimeDesc", "findAllPriceGrewUpInLastWeek")));
    }

    @Test
    public void testJpqlQuery() {
        executeTests(jpqlQueryRentalOfferRepository, new OrmMethodConfig(Set.of("findAllPriceGrewUpInLastWeek", "findAllModifiedAfterSortedByTimeDesc")));
    }

    @Test
    public void testNamedQuery() {
        executeTests(namedQueryRentalOfferRepository, new OrmMethodConfig(Set.of("findAllPriceGrewUpInLastWeek")));
    }

    @Test
    public void testCriteriaApi() {
        executeTests(criteriaRentalOfferRepository, new OrmMethodConfig());
    }

    private void executeTests(RentalOfferRepository rentalOfferRepository, OrmMethodConfig ormMethodConfig) {
        setup();

        RentalOffer testOffer = new RentalOffer(testName, "1212 AA", 1200.0, 21, "hello", false, "dsdsdas");
        List<OfferHistory> testHistory = List.of(new OfferHistory(Instant.now(), OfferStatus.NEW, null, testOffer),new OfferHistory(Instant.now(), OfferStatus.UPDATED, new FieldHistory("area", "51", "21"), testOffer));
        testOffer.setOfferHistories(testHistory);
        rentalOfferRepository.saveList(List.of(testOffer));

        List<ExecutionDetails> executionResults = new ArrayList<>();

        String name = "findBySearchStringIn";
        if (ormMethodConfig.isSupported(name)) {
            ExecutionDetails foundRentals = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name),
                    () -> rentalOfferRepository.findBySearchStringIn(List.of(testOffer.getSearchString())));
            executionResults.add(foundRentals);
            List<RentalOffer> foundByName = (List<RentalOffer>) foundRentals.getResult();
            assertTrue(!foundByName.isEmpty() && foundByName.stream().anyMatch(rentalOffer -> rentalOffer.getName().equals(testName)));
        }

        name = "findAllByNameContains";
        if (ormMethodConfig.isSupported(name)) {
            String containsStr = "My test 11";
            ExecutionDetails nameContainsDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name),
                    () -> rentalOfferRepository.findAllByNameContains(containsStr));
            executionResults.add(nameContainsDetails);
            List<RentalOffer> foundByNameContains = (List<RentalOffer>) nameContainsDetails.getResult();
            assertTrue(noDuplications(foundByNameContains));
            assertTrue(!foundByNameContains.isEmpty() && foundByNameContains.stream().allMatch(rentalOffer -> rentalOffer.getName().contains(containsStr)));
        }

        name = "findOfferHistoryByName";
        if (ormMethodConfig.isSupported(name)) {
            ExecutionDetails historyByNameDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name),
                    () -> rentalOfferRepository.findOfferHistoriesByOfferName(testName));
            executionResults.add(historyByNameDetails);
            RentalOffer historyByName = ((Optional<RentalOffer>) historyByNameDetails.getResult()).get();
            assertEquals(testHistory.size(), historyByName.getOfferHistories().size());
            assertTrue(historyByName.getOfferHistories().stream().allMatch(history -> history.getStatus() == OfferStatus.NEW || history.getStatus() == OfferStatus.UPDATED));
        }

        name = "findAllByAgencyPaged";
        if (ormMethodConfig.isSupported(name)) {
            int pageSize = 5;
            ExecutionDetails namePagedDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name) + "_1",
                    () -> rentalOfferRepository.findAllByAgencyPaged(testAgency, PageRequest.of(0, pageSize)));
            executionResults.add(namePagedDetails);
            Set<String> foundByNamePage1 = ((List<RentalOffer>) namePagedDetails.getResult()).stream().map(RentalOffer::getSearchString).collect(Collectors.toSet());
            ExecutionDetails namePagedDetails2 = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name) + "_2",
                    () -> rentalOfferRepository.findAllByAgencyPaged(testAgency, PageRequest.of(2,  pageSize)));
            executionResults.add(namePagedDetails);
            Set<String> foundByNamePage2 = ((List<RentalOffer>) namePagedDetails2.getResult()).stream().map(RentalOffer::getSearchString).collect(Collectors.toSet());
            assertEquals(((List<RentalOffer>) namePagedDetails.getResult()).size(), foundByNamePage1.size());
            assertEquals(((List<RentalOffer>) namePagedDetails2.getResult()).size(), foundByNamePage2.size());
            assertEquals(pageSize, foundByNamePage1.size());
            assertEquals(pageSize, foundByNamePage2.size());
            assertTrue(noDuplications((List<RentalOffer>) namePagedDetails.getResult()));
            assertTrue(noDuplications((List<RentalOffer>) namePagedDetails2.getResult()));
//            assertTrue(foundByNamePage1.stream().noneMatch(foundByNamePage2::contains)); //doesn't work for CriteriaApiRepository
        }

        name = "findAllByPriceGreaterThanAndAreaLessThan";
        if (ormMethodConfig.isSupported(name)) {
            //add limitations
            double minPrice = 1900.0;
            int maxArea = 20;
            ExecutionDetails multipleFiltersDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), () -> rentalOfferRepository.findAllByPriceGreaterThanAndAreaLessThan(minPrice, maxArea));
            executionResults.add(multipleFiltersDetails);
            List<RentalOffer> multipleFiltersOffers = (List<RentalOffer>) multipleFiltersDetails.getResult();
            assertTrue(noDuplications(multipleFiltersOffers));
            assertTrue(multipleFiltersOffers.stream().allMatch(rentalOffer -> rentalOffer.getPrice() >= minPrice && rentalOffer.getArea() <= maxArea));
        }

        name = "findAllUpdatedAfter";
        if (ormMethodConfig.isSupported(name)) {
            Instant timeToCheck = Instant.now().minus(3, ChronoUnit.DAYS);
            ExecutionDetails updatedAfterDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), () -> rentalOfferRepository.findAllUpdatedAfter(timeToCheck));
            executionResults.add(updatedAfterDetails);
            List<RentalOffer> updatedAfterOffers = (List<RentalOffer>) updatedAfterDetails.getResult();
            assertTrue(noDuplications(updatedAfterOffers));
            assertTrue(updatedAfterOffers.stream().allMatch(rentalOffer -> rentalOffer.getOfferHistories().stream().anyMatch(offerHistory -> offerHistory.getStatus() == OfferStatus.UPDATED && offerHistory.getTime().isAfter(timeToCheck))));
        }

        name = "findAllModifiedAfterSortedByTimeDesc";
        if (ormMethodConfig.isSupported(name)) {
            Instant timeToCheck = Instant.now().minus(3, ChronoUnit.DAYS);
            ExecutionDetails updatedAfterSortedDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), () -> rentalOfferRepository.findAllModifiedAfterSortedByTimeDesc(timeToCheck));
            executionResults.add(updatedAfterSortedDetails);
            List<ImmutablePair<RentalOffer, OfferHistory>> updatedAfterSortedOffers = (List<ImmutablePair<RentalOffer, OfferHistory>>) updatedAfterSortedDetails.getResult();
            assertTrue(IntStream.range(0, updatedAfterSortedOffers.size() - 1).allMatch(i -> isSortedByTimeDesc(updatedAfterSortedOffers.get(i).right, updatedAfterSortedOffers.get(i + 1).right)));
        }

        name = "findAllPriceGrewUpInLastWeek";
        if (ormMethodConfig.isSupported(name)) {
            ExecutionDetails priceGrewUpDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), rentalOfferRepository::findAllPriceGrewUpInLast2WeeksLimit5000);
            executionResults.add(priceGrewUpDetails);
            List<RentalOffer> rentalOffers = (List<RentalOffer>) priceGrewUpDetails.getResult();
            assertTrue(noDuplications(rentalOffers));
            Set<Integer> ids = rentalOffers.stream().map(RentalOffer::getId).collect(Collectors.toSet());
            List<OfferHistory> offerHistories = offerHistoryRepository.findByRentalOffer_IdIn(ids)
                    .stream()
                    .filter(offerHistory -> offerHistory.getTime().isAfter(Instant.now().minus(14, ChronoUnit.DAYS)) && offerHistory.getFieldHistory().getFieldName().equals("price"))
                    .collect(Collectors.toList());
            Map<Integer, List<OfferHistory>> offerHistoriesMap = offerHistories.stream()
                    .collect(Collectors.groupingBy(offerHistory -> offerHistory.getRentalOffer().getId()));
            assertTrue(offerHistoriesMap.entrySet().stream().allMatch(entry ->
                    entry.getValue().stream().map(offerHistory ->
                            offerHistory.getFieldHistory().getDelta()).reduce(0D, Double::sum) > 0
            ));
        }

        name = "countAll";
        if (ormMethodConfig.isSupported(name)) {
            ExecutionDetails countAllDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), rentalOfferRepository::countAll);
            executionResults.add(countAllDetails);
            long countAllResult = (long) countAllDetails.getResult();
            assertEquals(100001, countAllResult);
        }

        name = "countCreatedInLastMonth";
        if (ormMethodConfig.isSupported(name)) {
            ExecutionDetails countCreatedLastMonthDetails = executeLogged(getDisplayedExecutionMethodName(rentalOfferRepository, name), rentalOfferRepository::countCreatedInLastMonth);
            executionResults.add(countCreatedLastMonthDetails);
            long createdLastMonthResults = (long) countCreatedLastMonthDetails.getResult();
        }

        executionResults.forEach(details-> {
            System.out.println("Method " + details.getFunctionName() + ", time " + details.getDuration());
        });
    }

    private boolean isSortedByTimeDesc(OfferHistory offerHistory1, OfferHistory offerHistory2) {
        return offerHistory1.getTime().isAfter(offerHistory2.getTime()) || offerHistory1.getTime().equals(offerHistory2.getTime());
    }

    private boolean noDuplications(List<RentalOffer> rentalOffers) {
        return rentalOffers.stream().map(RentalOffer::getId).collect(Collectors.toSet()).size() == rentalOffers.size();
    }

    private String getDisplayedExecutionMethodName(RentalOfferRepository offerRepository, String name) {
        return offerRepository.getName() + "_" + name;
    }

    private void setup() {
        if (!isSetup) {
            List<String> agencies = jpqlQueryRentalOfferRepository.findDistinctAgencies();
            testAgency = agencies.get(0);
            jpqlQueryRentalOfferRepository.deleteAll(jpqlQueryRentalOfferRepository.findAllByNameContains(testName));
        }
    }


    private ExecutionDetails executeLogged(String name, Supplier<Object> function) {
        long start = System.nanoTime() / 1000;
        Object results = function.get();
        long end = System.nanoTime() / 1000;
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
