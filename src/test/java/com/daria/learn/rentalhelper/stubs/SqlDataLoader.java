package com.daria.learn.rentalhelper.stubs;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentals.domain.OfferHistory;
import com.daria.learn.rentalhelper.rentals.domain.OfferStatus;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferFieldNames;
import com.daria.learn.rentalhelper.rentals.persist.RentalOfferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.daria.learn.rentalhelper.rentals.domain.RentalOfferFieldNames.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Use it once for data uploading
 */
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE})
public class SqlDataLoader {

    private static final String chars = "abcdefghjklmnopqrstuyxvwz";
    private static final String uppercase = "ABCDEFGHJKLMOPQRSTUYXWZ";
    private static final String numbers = "1234567890";

    private final OfferStatus[] statuses = OfferStatus.values();
    private final List<String> fields = RentalOfferFieldNames.getAll();
    private final List<String> agencies = new ArrayList<>();
    private final String SOURCE = "pararius";

    public void setupAgencies() {
        if (!agencies.isEmpty())
            return;
        int amount = 100;
        for (int i = 0; i < amount; i++) {
            agencies.add(getRandomString(0));
        }
    }

    @Autowired
    private RentalOfferRepository rentalOfferRepository;

    @Test
    public void loadRandomData() {
        setupAgencies();

        Random r = new Random();
        int numberOfRecords = 50_000;
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
                        true, getRandomString(50), SOURCE);
                List<OfferHistory> histories1 = getRandomHistories(historySize, offer);
                offer.setOfferHistories(histories1);
                offerList.add(offer);
            }
            rentalOfferRepository.saveAll(offerList);
        }

        assertTrue(rentalOfferRepository.count() >= numberOfRecords);
    }

    private List<OfferHistory> getRandomHistories(int historySize, RentalOffer rentalOffer) {
        List<OfferHistory> histories = new ArrayList<>();
        Random r = new Random();
        for (int k = 0; k < historySize; k++) {
            String fieldName = fields.get(r.nextInt(fields.size()));
            histories.add(getOfferHistoryFromField(fieldName, rentalOffer));
        }
        return histories;
    }

    private static Instant getRandomTime() {
        return Instant.now().minus(new Random().nextInt(120), ChronoUnit.DAYS);
    }

    private static OfferHistory getOfferHistoryFromField(String field, RentalOffer offer) {
        Instant creationTime = getRandomTime();
        switch (field) {
            case NAME_FIELD:
                return new OfferHistory(creationTime, NAME_FIELD, getRandomString(10), getRandomString(15), offer);
            case PRICE_FIELD:
                return new OfferHistory(creationTime, PRICE_FIELD, String.valueOf(getRandomNumber(0, 2000)), String.valueOf(getRandomNumber(0, 2000)), offer);
            case IS_FURNISHED_FIELD: {
                Random r = new Random();
                return new OfferHistory(creationTime, RentalOfferFieldNames.IS_FURNISHED_FIELD, String.valueOf(r.nextBoolean()), String.valueOf(r.nextBoolean()), offer);
            }
            case AVAILABLE_FROM_FIELD:
                return new OfferHistory(creationTime, RentalOfferFieldNames.AVAILABLE_FROM_FIELD, String.valueOf(getRandomTime()), String.valueOf(getRandomTime()), offer);
        }
        throw new RuntimeException("Unsupported field " + field);
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
            sb.append(chars.charAt(r.nextInt(chars.length())));
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

    private static int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt(max) + min;
    }
}
