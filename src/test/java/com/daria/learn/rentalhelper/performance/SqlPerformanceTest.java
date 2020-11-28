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
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.JPA_METHOD_PROFILE})
public class SqlPerformanceTest {

//    @Autowired
    private JpaMethodRentalOfferRepositoryAdapter jpaMethodRentalOfferRepository;
//    @Autowired
    private JpaQueryRentalOfferRepository jpaQueryRentalOfferRepository;
//    @Autowired
    private NamedQueryRentalOfferRepository namedQueryRentalOfferRepository;

    @Test
    public void testJpaMethod() {
        String testName = "Apartment My test 11";
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
        ExecutionDetails nameContainsDetails = executeLogged("findAllByNameContains", () -> jpaMethodRentalOfferRepository.findAllByNameContains(containsStr));
        executionResults.add(nameContainsDetails);
        List<RentalOffer> foundByNameContains = (List<RentalOffer>)foundRentals.getResult();
        assertTrue(!foundByNameContains.isEmpty() && foundByNameContains.stream().allMatch(rentalOffer -> rentalOffer.getName().contains(containsStr)));


        ExecutionDetails namePagedDetails = executeLogged("findAllByNamePaged_1", () -> jpaMethodRentalOfferRepository.findAllByAgencyPaged(testName, PageRequest.of(0, 5)));
        executionResults.add(namePagedDetails);
        List<RentalOffer> foundByNamePage1 = (List<RentalOffer>)foundRentals.getResult();
        ExecutionDetails namePagedDetails2 = executeLogged("findAllByNamePaged_2", () -> jpaMethodRentalOfferRepository.findAllByAgencyPaged(testName, PageRequest.of(2, 5)));
        executionResults.add(namePagedDetails);
        List<RentalOffer> foundByNamePage2 = (List<RentalOffer>)foundRentals.getResult();

        executeLogged("", () -> jpaMethodRentalOfferRepository.findAllByPriceGreaterThanAndAreaLessThan(1500.0, 90));

        executeLogged("", () -> jpaMethodRentalOfferRepository.findAllUpdatedAfter(Instant.now().minus(1, ChronoUnit.MONTHS)));

        executeLogged("", () -> jpaMethodRentalOfferRepository.findAllUpdatedByFieldName("area"));

        assertThrows(UnsupportedOperationException.class,() -> executeLogged("", () -> jpaMethodRentalOfferRepository.findAllPriceGrewUpInLastMonth()));

        executeLogged("", () -> jpaMethodRentalOfferRepository.countAll());

        executeLogged("", () -> jpaMethodRentalOfferRepository.countCreatedInLastMonth());

        executeLogged("", () -> jpaMethodRentalOfferRepository.findOfferHistoryByName(testName));
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
