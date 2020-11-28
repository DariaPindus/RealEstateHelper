package com.daria.learn.rentalhelper.performance;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.FieldHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.persist.jpa.JpaMethodRentalOfferRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Use it once for data uploading
 */
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE, ApplicationProfiles.JPA_METHOD_PROFILE})
public class SqlDataLoader {

    private static final String chars = "abcdefghjklmnopqrstuyxvwz";
    private static final String uppercase = "ABCDEFGHJKLMOPQRSTUYXWZ";
    private static final String numbers = "1234567890";

    private final OfferStatus[] statuses = OfferStatus.values();
    private final List<String> fields = List.of("price", "link", "area");
    private final List<String> agencies = new ArrayList<>();

    public void setupAgencies() {
        if (!agencies.isEmpty())
            return;
        int amount = 100;
        for (int i = 0; i < amount; i++) {
            agencies.add(getRandomString(0));
        }
    }

    @Autowired
    private JpaMethodRentalOfferRepository jpaMethodRentalOfferRepository;

    @Test
    public void loadRandomData() {
        setupAgencies();

        Random r = new Random();
        int numberOfRecords = 100_000;
        int batchSize = 10000;
        int maxNumberOfHistory = 5;
        for (int i = 0; i < numberOfRecords / batchSize; i++) {
            int size = i == numberOfRecords / batchSize - 1 ? numberOfRecords - i * batchSize: batchSize;
            List<RentalOffer> offerList = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                int historySize = r.nextInt(maxNumberOfHistory) + 1;
                double price = 10 + (2000 - 10) * r.nextDouble();
                RentalOffer offer = new RentalOffer(getRandomName(), getRandomCode(), price,
                        r.nextInt(200) + 10, agencies.get(r.nextInt(agencies.size() - 1)),
                        true, getRandomString(50));
                List<OfferHistory> histories1 = getRandomHistories(historySize, offer);
                offer.setOfferHistories(histories1);
                offerList.add(offer);
            }
            jpaMethodRentalOfferRepository.saveAll(offerList);
        }

        assertTrue(jpaMethodRentalOfferRepository.count() >= numberOfRecords);
    }

    private List<OfferHistory> getRandomHistories(int historySize, RentalOffer rentalOffer) {
        List<OfferHistory> histories = new ArrayList<>();
        for (int k = 0; k < historySize; k++) {
            Instant creationTime = Instant.now().minus(new Random().nextInt(40), ChronoUnit.DAYS);
            FieldHistory fieldHistory = new FieldHistory(fields.get(new Random().nextInt(fields.size())), String.valueOf(new Random().nextInt()), String.valueOf(new Random().nextInt()));
            histories.add(new OfferHistory(creationTime, statuses[new Random().nextInt(statuses.length - 1)], fieldHistory, rentalOffer));
        }
        return histories;
    }

    private static String getRandomName() {
        boolean isApartment = new Random().nextBoolean();
        return (isApartment ? "Apartment " : "Room ") + getRandomString(0);
    }

    private static String getRandomString(int length) {
        Random r = new Random();
        int actLength = length == 0 ? r.nextInt(10) + 3 : length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < actLength; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length() - 1)));
        }
        return sb.toString();
    }

    private static String getRandomCode() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(numbers.charAt(r.nextInt(numbers.length() - 1)));
        }
        sb.append(" ");
        for (int i = 0; i < 2; i++) {
            sb.append(uppercase.charAt(r.nextInt(uppercase.length() - 1)));
        }
        return sb.toString();
    }
}
