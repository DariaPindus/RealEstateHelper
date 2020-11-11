package com.daria.learn.rentalhelper.rentals.communication.web;

import com.daria.learn.rentalhelper.rentals.RentalNotificationFacade;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RentalController {

    private final RentalNotificationFacade notificationFacade;

    public RentalController(RentalNotificationFacade notificationFacade) {
        this.notificationFacade = notificationFacade;
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
}
