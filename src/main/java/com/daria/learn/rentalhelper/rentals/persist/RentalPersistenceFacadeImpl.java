package com.daria.learn.rentalhelper.rentals.persist;

import com.daria.learn.rentalhelper.rentals.domain.RentalOffer;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
                    .map(RentalOffer::fromRentalOfferDTO).collect(toList());
            rentalOfferRepository.saveAll(newOffers);

            log.info("Persisted and should notify about new offers {} ", newOffers);
            return rentalOfferDTOS; //TODO: Stub!
        } catch (Exception ex) {
            log.error("Error persisting rental offers: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }



    @Override
    public List<String> getSourceOpenOffersUrls(String source) {
        return rentalOfferRepository.findOpenRentalOffers(source).stream().map(RentalOffer::getLink).collect(toList());
    }

    @Override
    public List<RentalOfferDTO> updateRentalDetails(List<RentalOfferDetailsDTO> allRentals) {
        Map<String, RentalOfferDetailsDTO> rentalWithLinks = allRentals.stream().collect(toMap(RentalOfferDetailsDTO::getLink, offerDTO -> offerDTO));
        List<RentalOffer> dbOffers = rentalOfferRepository.findByLinkIn(rentalWithLinks.keySet());
        //TODO: !!! try not to use additional collection
        List<RentalOffer> toUpdate = new LinkedList<>();

        dbOffers.forEach(dbOffer -> {
            if (!rentalWithLinks.containsKey(dbOffer.getLink()))
                return;
            RentalOfferDetailsDTO detailsDTO = rentalWithLinks.get(dbOffer.getLink())
        });
    }
}
