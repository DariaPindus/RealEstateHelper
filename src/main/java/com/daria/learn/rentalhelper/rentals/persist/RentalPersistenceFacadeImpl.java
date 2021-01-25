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
                        String searchString = RentalOffer.generateSearchStringFromDTO(dto);
                        if (rentalSearchInfos.containsKey(searchString))
                            return;
                        rentalSearchInfos.put(searchString, dto);
                    }
            );

            Set<String> existingOffersSearchStrings = rentalOfferRepository.findBySearchStringIn(rentalSearchInfos.keySet())
                    .stream()
                    .map(RentalOffer::getSearchString)
                    .collect(Collectors.toSet());

            List<RentalOffer> newOffers = rentalSearchInfos.values().stream()
                    .filter(rentalOfferDTO -> !existingOffersSearchStrings.contains(RentalOffer.generateSearchStringFromDTO(rentalOfferDTO)))
                    .map(RentalOffer::fromRentalOfferDTO).collect(Collectors.toList());
            rentalOfferRepository.saveAll(newOffers);

            log.info("Persisted and should notify about new offers {} ", newOffers);
            return rentalOfferDTOS; //TODO: Stub!
        } catch (Exception ex) {
            log.error("Error persisting rental offers: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getOpenOffersUrls() {
        return rentalOfferRepository.findOpenRentalOffers().stream().map(RentalOffer::getLink).collect(Collectors.toList());
    }
}
