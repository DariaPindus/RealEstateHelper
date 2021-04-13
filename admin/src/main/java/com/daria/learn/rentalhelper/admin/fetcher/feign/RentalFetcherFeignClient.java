package com.daria.learn.rentalhelper.admin.fetcher.feign;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("rentalfetcher")
public interface RentalFetcherFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = "/offers")
    List<BriefRentalOfferDTO> getAll(@RequestParam String from);
}
