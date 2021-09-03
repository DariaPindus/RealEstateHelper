package com.daria.learn.rentalhelper.core.persist;

import com.daria.learn.rentalhelper.core.domain.ParariusRentalOffer;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.core.domain.RentalOffer;
import com.daria.learn.rentalhelper.dtos.FetchDetailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class RentalOfferFacadeImpl implements RentalOfferFacade {

    private static final Logger log = LoggerFactory.getLogger(RentalOfferFacadeImpl.class);
    private final RentalOfferRepository rentalOfferRepository;

    public RentalOfferFacadeImpl(RentalOfferRepository rentalOfferRepository) {
        this.rentalOfferRepository = rentalOfferRepository;
    }

    @Override
    @Transactional
    public void persistNewRentals(final List<BriefRentalOfferDTO> briefRentalOfferDTOS) {
        try {
            Map<String, BriefRentalOfferDTO> rentalLinks = briefRentalOfferDTOS.stream()
                    .collect(
                            toMap(BriefRentalOfferDTO::getLink,
                                    rentalDTO -> rentalDTO,
                                    (rentalDTO1, rentalTO2) -> {
                                        log.warn("Duplicate rental DTO: {}, {}", rentalDTO1, rentalTO2);
                                        return rentalDTO1;
                                    }));

            Set<String> existingLinks = rentalOfferRepository.findByLinkIn(rentalLinks.keySet())
                    .stream()
                    .map(RentalOffer::getLink)
                    .collect(Collectors.toSet());

            List<RentalOffer> newOffers = rentalLinks.values().stream()
                    .filter(rentalOfferDTO -> !existingLinks.contains(rentalOfferDTO.getLink()))
                    .map(RentalOffer::fromBriefRentalOfferDTO).collect(toList());
            rentalOfferRepository.saveAll(newOffers);

            log.info("Persisted new offers {} ", newOffers);
        } catch (Exception ex) {
            log.error("Error persisting rental offers: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    //TODO: pagination ?
    @Override
    public List<FetchDetailRequestDTO> getFetchDetailRequests() {
        List<String> parariusOffersUrls = rentalOfferRepository.findBySource(ParariusRentalOffer.DISCRIMINATOR_VALUE).stream()
                .map(RentalOffer::getLink)
                .collect(toUnmodifiableList());
        return List.of(
                new FetchDetailRequestDTO(ParariusRentalOffer.DISCRIMINATOR_VALUE, parariusOffersUrls)
        );
    }

    @Override
    @Transactional
    //TODO: move logic for shouldbenotifiedabout ?
    public List<DetailRentalOffersDTO> updateRentalDetails(List<DetailRentalOffersDTO> allRentals) {
        Map<String, DetailRentalOffersDTO> newRentalWithLinks = allRentals.stream().collect(toMap(DetailRentalOffersDTO::getLink, offerDTO -> offerDTO));

        List<RentalOffer> existingOffers = rentalOfferRepository.findByLinkIn(new ArrayList<>(newRentalWithLinks.keySet()));
        List<DetailRentalOffersDTO> toNotifyAbout = new LinkedList<>();

        for (RentalOffer existingOffer : existingOffers) {
            try {
                if (!newRentalWithLinks.containsKey(existingOffer.getLink()))
                    continue;
                DetailRentalOffersDTO detailsDTO = newRentalWithLinks.get(existingOffer.getLink());
                existingOffer.updateFromDetails(detailsDTO);
                if (existingOffer.shouldBeNotifiedAbout())
                    toNotifyAbout.add(existingOffer.toRentalOfferDetailsDTO());
            } catch (Exception ex) {
                log.warn("Couldn't update offer {}", existingOffer);
            }
        }

        rentalOfferRepository.saveAll(existingOffers);
        log.info("Update rental details, should be notified about (" + toNotifyAbout.size() + ") : " + toNotifyAbout);

        return toNotifyAbout;
    }
}
