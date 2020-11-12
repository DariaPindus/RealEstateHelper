package com.daria.learn.rentalhelper.rentals.communication.web;

import com.daria.learn.rentalhelper.rentals.RentalNotificationFacade;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import com.daria.learn.rentalhelper.rentals.jobs.ScheduledRentalFetchingJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RentalController {

    private final RentalNotificationFacade notificationFacade;
    private final ScheduledRentalFetchingJob job;
    private final String SECRET_HEADER;

    public RentalController(RentalNotificationFacade notificationFacade, ScheduledRentalFetchingJob job, @Value("${controller.header.x-secret.token}") String secretHeader) {
        this.notificationFacade = notificationFacade;
        this.job = job;
        this.SECRET_HEADER = secretHeader;
    }

    @PostMapping("/rentals")
    public ResponseEntity<Void> postNewRentalOffer(@RequestBody RentalOffersListDTO rentalOfferDTOs) {
        try {
            notificationFacade.saveAndNotifyNewRentals(rentalOfferDTOs.getRentalOfferDTOS());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception ex) {
            System.out.println("[post new offers] " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@RequestHeader("X-Secret") String secret) {
        if (secret == null || !secret.equals(SECRET_HEADER))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        new Thread(job::fetchRentals).start();
        return ResponseEntity.ok().build();
    }
}
