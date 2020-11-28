package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalPersistenceFacadeImpl implements RentalPersistenceFacade {

    private static final Logger log = LoggerFactory.getLogger(RentalPersistenceFacadeImpl.class);
    private final RentalOfferRepository rentalOfferRepository;

    public RentalPersistenceFacadeImpl(RentalOfferRepository rentalOfferRepository) {
        this.rentalOfferRepository = rentalOfferRepository;
    }

    @Override
    @Transactional
    public List<RentalOfferDTO> persistNewRentals(final List<RentalOfferDTO> rentalOfferDTOS) {
        try {
            Map<String, RentalOfferDTO> rentalSearchInfos = new HashMap<>();
            rentalOfferDTOS.forEach(
                    dto -> {
                        String searchString = RentalOffer.generateSearchString(dto.getPostalCode(), dto.getArea(), dto.getAgency());
                        if (rentalSearchInfos.containsKey(searchString))
                            return;
                        rentalSearchInfos.put(searchString, dto);
                    }
            );

            List<RentalOffer> existingOffers = rentalOfferRepository.findBySearchStringIn(rentalSearchInfos.keySet());
            List<RentalOffer> offersToPersist = new LinkedList<>();
            List<RentalOfferDTO> offersToNotifyAbout = new ArrayList<>();

            existingOffers.forEach(offer -> {
                RentalOfferDTO offerDTO = rentalSearchInfos.get(offer.getSearchString());
                RentalOffer offerFromDTO = RentalOffer.fromRentalOfferDTO(offerDTO);
                if (!offer.equals(offerFromDTO))
                    return;
                boolean offerWasChanged = offer.updateIfChanged(offerFromDTO);
                if (offerWasChanged) {
                    offersToPersist.add(offer);
                    offersToNotifyAbout.add(offerDTO);
                }
                rentalSearchInfos.remove(offer.getSearchString());
            });

            List<RentalOffer> newOffers = rentalSearchInfos.values().stream().map(RentalOffer::fromRentalOfferDTO).collect(Collectors.toList());
            offersToPersist.addAll(newOffers);
            rentalOfferRepository.saveList(offersToPersist);

            offersToNotifyAbout.addAll(rentalSearchInfos.values());
            log.info("Persisted and should notify about new offers {} ", offersToNotifyAbout);
            return offersToNotifyAbout;
        } catch (Exception ex) {
            log.error("Error persisting rental offers: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
