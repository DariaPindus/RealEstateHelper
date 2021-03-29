package com.daria.learn.rentalhelper.rentalfetcher;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import com.daria.learn.rentalhelper.rentalfetcher.communication.message.RentalNotificationSender;
import com.daria.learn.rentalhelper.rentalfetcher.fetch.FetcherFacade;
import com.daria.learn.rentalhelper.rentalfetcher.jobs.ScheduledDetailsUpdateJob;
import com.daria.learn.rentalhelper.rentalfetcher.persist.RentalPersistenceFacade;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.List;

import static com.daria.learn.rentalhelper.rentalfetcher.Random.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ScheduledDetailsUpdateJobTest {

    @MockBean
    private FetcherFacade fetcherFacade;
    @MockBean
    private RentalPersistenceFacade rentalPersistenceFacade;
    @MockBean
    private RentalNotificationSender rentalSender;
    private ScheduledDetailsUpdateJob updateJob = new ScheduledDetailsUpdateJob(fetcherFacade, rentalPersistenceFacade, rentalSender);

    private List<BriefRentalOfferDTO> testRentalOffers;

    @BeforeAll
    public void setup() {
        Mockito.when(fetcherFacade.fetchOffers()).thenReturn(
                List.of(
                        new BriefRentalOfferDTO("Appartment test 1", "1112 AA", 34, "My agency", "/some/link/1", 1111, "source"),
                        new BriefRentalOfferDTO("Appartment test 2", "1112 AB", 41, "My agency", "/some/link/2", 1000, "source"),
                        new BriefRentalOfferDTO("Appartment test 3", "1212 AA", 44, "Another agency", "/some/link/3", 999, "source")));
        Mockito.when(rentalPersistenceFacade.getSourceOpenOffersUrls(any())).thenReturn(List.of());
        Mockito.when(fetcherFacade.fetchOfferDetailFromSource(any(), any())).thenAnswer((Answer<RentalOfferDetailsDTO>) invocationOnMock -> {
            Thread.sleep(200);
            return new RentalOfferDetailsDTO(
                    getRandomOfferName(), getRandomString(10), RentalStatusDTO.AVAILABLE,
                    getRandomPostalCode(), (double)getRandomNumber(500, 2000), false,
                    Instant.now(), false, getRandomNumber(20, 200), getRandomString(10));
        });
    }

    public void test_runUpdateRentalOffers() {
        updateJob.runUpdateRentalOffers();
    }
}
