package com.daria.learn.rentalhelper.admin.fetcher.web;

import com.daria.learn.rentalhelper.admin.fetcher.feign.RentalFetcherFeignClient;
import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class FetcherAdminController {

    private final RentalFetcherFeignClient rentalFetcherFeignClient;

    public FetcherAdminController(RentalFetcherFeignClient rentalFetcherFeignClient) {
        this.rentalFetcherFeignClient = rentalFetcherFeignClient;
    }

    @GetMapping("/offers/lastweek")
    public List<BriefRentalOfferDTO> getAllInLastWeek() {
        Instant from = Instant.now().minus(7, ChronoUnit.DAYS);
        return rentalFetcherFeignClient.getAll(from.toString());
    }
}
