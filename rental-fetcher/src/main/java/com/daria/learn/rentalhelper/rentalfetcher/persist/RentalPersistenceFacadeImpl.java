package com.daria.learn.rentalhelper.rentalfetcher.persist;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class RentalPersistenceFacadeImpl implements RentalPersistenceFacade {

    private static final Logger log = LoggerFactory.getLogger(RentalPersistenceFacadeImpl.class);
    private final RentalOfferRepository rentalOfferRepository;
    private final TestHystrix testHystrix;

    public RentalPersistenceFacadeImpl(RentalOfferRepository rentalOfferRepository, TestHystrix testHystrix) {
        this.rentalOfferRepository = rentalOfferRepository;
        this.testHystrix = testHystrix;
    }

    @Override
    @Transactional
    public List<BriefRentalOfferDTO> persistNewRentals(final List<BriefRentalOfferDTO> briefRentalOfferDTOS) {
        try {
            Map<String, BriefRentalOfferDTO> rentalSearchInfos = new HashMap<>();
            briefRentalOfferDTOS.forEach(
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
                    .map(RentalOffer::fromBriefRentalOfferDTO).collect(toList());
            rentalOfferRepository.saveAll(newOffers);

            log.info("Persisted new offers {} ", newOffers);
            return briefRentalOfferDTOS;
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
    @Transactional
    //TODO: move logic for shouldbenotifiedabout ?
    public List<RentalOfferDetailsDTO> updateRentalDetails(List<RentalOfferDetailsDTO> allRentals) {
        Map<String, RentalOfferDetailsDTO> newRentalWithLinks = allRentals.stream().collect(toMap(RentalOfferDetailsDTO::getLink, offerDTO -> offerDTO));

        List<RentalOffer> existingOffers = testHystrix.getExistingOffersFromLinksSet(newRentalWithLinks.keySet());
        List<RentalOfferDetailsDTO> toNotifyAbout = new LinkedList<>();

        for (RentalOffer existingOffer : existingOffers) {
            if (!newRentalWithLinks.containsKey(existingOffer.getLink()))
                continue;
            RentalOfferDetailsDTO detailsDTO = newRentalWithLinks.get(existingOffer.getLink());
            existingOffer.updateFromDetails(detailsDTO);
            if (existingOffer.shouldBeNotifiedAbout())
                toNotifyAbout.add(existingOffer.toRentalOfferDetailsDTO());
        }

        rentalOfferRepository.saveAll(existingOffers);
        log.info("Update rental details, should be notified about (" + toNotifyAbout.size() + ") : " + toNotifyAbout);

        return toNotifyAbout;
    }

    @Override
    public List<BriefRentalOfferDTO> getAllAfter(Instant time) {
        return rentalOfferRepository.findAllByCreationTimeAfter(time).stream().map(RentalOffer::toBriefRentalOfferDTO).collect(toUnmodifiableList());
    }
}
