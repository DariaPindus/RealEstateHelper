package com.daria.learn.rentalhelper.rentalfetcher.communication.web;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentalfetcher.ApplicationProfiles;
import com.daria.learn.rentalhelper.rentalfetcher.jobs.ScheduledRentalFetchingJob;
import com.daria.learn.rentalhelper.rentalfetcher.persist.RentalPersistenceFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@Profile(ApplicationProfiles.NOT_TEST_PROFILE)
public class RentalController {

    private final ScheduledRentalFetchingJob job;
    private final String SECRET_HEADER;
    private final RentalPersistenceFacade rentalPersistenceFacade;

    public RentalController(ScheduledRentalFetchingJob job, @Value("${controller.header.x-secret.token}") String secretHeader, RentalPersistenceFacade rentalPersistenceFacade) {
        this.job = job;
        this.SECRET_HEADER = secretHeader;
        this.rentalPersistenceFacade = rentalPersistenceFacade;
    }

    //TODO:
//    @PostMapping("/rentals")
//    public ResponseEntity<Void> postNewRentalOffer(@RequestHeader("X-Secret") String secret, @RequestBody RentalOffersListDTO rentalOfferDTOs) {
//        if (secret == null || !secret.equals(SECRET_HEADER))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        try {
//            notificationFacade.saveAndNotifyNewRentals(rentalOfferDTOs.getRentalOfferDTOS());
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } catch (Exception ex) {
//            System.out.println("[post new offers] " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @GetMapping("/offers")
    public List<BriefRentalOfferDTO> getAll(@RequestParam String from) {
        Instant fromTime = Instant.parse(from);
        return rentalPersistenceFacade.getAllAfter(fromTime);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@RequestHeader("X-Secret") String secret) {
        if (secret == null || !secret.equals(SECRET_HEADER))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        new Thread(job::fetchRentals).start();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OK");
    }
}
