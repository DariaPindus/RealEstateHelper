package com.daria.learn.rentalhelper.rentals;

import com.daria.learn.rentalhelper.rentals.communication.message.RentalNotificationSender;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOffersListDTO;
import com.daria.learn.rentalhelper.rentals.persist.RentalPersistenceFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RentalNotificationFacadeImpl implements RentalNotificationFacade {

    private final RentalNotificationSender rentalSender;
    private final RentalPersistenceFacade persistenceFacade;

    public RentalNotificationFacadeImpl(RentalNotificationSender rentalSender, RentalPersistenceFacade persistenceFacade) {
        this.rentalSender = rentalSender;
        this.persistenceFacade = persistenceFacade;
    }

    public void saveAndNotifyNewRentals(List<RentalOfferDTO> offerDTOList) {
        List<RentalOfferDTO> newRentalOffers = persistenceFacade.persistNewRentals(offerDTOList);
        rentalSender.sendMessage(new RentalOffersListDTO(newRentalOffers));
    }

}
