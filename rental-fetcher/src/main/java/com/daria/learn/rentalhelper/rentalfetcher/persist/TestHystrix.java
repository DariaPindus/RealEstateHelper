package com.daria.learn.rentalhelper.rentalfetcher.persist;

import com.daria.learn.rentalhelper.rentalfetcher.domain.RentalOffer;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TestHystrix {
    @Autowired
    private RentalOfferRepository rentalOfferRepository;

    @HystrixCommand
    public List<RentalOffer> getExistingOffersFromLinksSet(Set<String> links) {
        try {
            Thread.sleep(2000);
            return rentalOfferRepository.findByLinkIn(links);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
