package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RentalPersistenceFacadeImpl implements RentalPersistenceFacade {

    private final RentalOfferRepository rentalOfferRepository;

    public RentalPersistenceFacadeImpl(RentalOfferRepository rentalOfferRepository) {
        this.rentalOfferRepository = rentalOfferRepository;
    }

    @Override
    @Transactional
    public void persistRentals(final List<RentalOfferDTO> rentalOfferDTOS) {
        try {
            Map<String, RentalOfferDTO> rentalSearchInfos = rentalOfferDTOS.stream()
                    .collect(Collectors.toMap(dto -> RentalOffer.generateSearchString(dto.getPostalCode(), dto.getArea(), dto.getAgency()), dto -> dto));

            List<RentalOffer> existingOffers = rentalOfferRepository.findBySearchStringIn(rentalSearchInfos.keySet());
            List<RentalOffer> existingOffersToUpdate = new LinkedList<>();

            existingOffers.forEach(offer -> {
                RentalOfferDTO offerDTO = rentalSearchInfos.get(offer.getSearchString());
                RentalOffer offerFromDTO = RentalOffer.fromRentalOfferDTO(offerDTO);
                if (!offer.equals(offerFromDTO))
                    return;
                boolean wasAdded = offer.addOfferHistoryIfNeeded(offerFromDTO);
                rentalSearchInfos.remove(offer.getSearchString());
                if (wasAdded) existingOffersToUpdate.add(offer);
            });

            List<RentalOffer> newOffers = rentalSearchInfos.values().stream().map(RentalOffer::fromRentalOfferDTO).collect(Collectors.toList());

            rentalOfferRepository.saveAll(existingOffersToUpdate);
            rentalOfferRepository.saveAll(newOffers);
        } catch (Exception ex) {
            System.out.println("[persistRentals] error " + ex.getMessage());
        }
    }
}
