package com.daria.learn.rentalhelper.core.jobs;

import com.daria.learn.rentalhelper.core.ApplicationProfiles;
import com.daria.learn.rentalhelper.core.communication.message.RentalFetchSender;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile(ApplicationProfiles.NOT_TEST_PROFILE)
@Component
public class ScheduledRentalFetchingJob {

    private final RentalFetchSender rentalFetchSender;

    public ScheduledRentalFetchingJob(RentalFetchSender rentalFetchSender) {
        this.rentalFetchSender = rentalFetchSender;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchRentals() {
        rentalFetchSender.sendGeneralFetchRequest();
    }
}
