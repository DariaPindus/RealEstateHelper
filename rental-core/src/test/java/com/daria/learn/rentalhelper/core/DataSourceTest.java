package com.daria.learn.rentalhelper.core;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataSourceTest {

/*
    private DataSource parariusDataSource = new ParariusSource();
    private List<BriefRentalOfferDTO> briefRentalOfferDTOList = null;

    @Test
    @Order(1)
    public void test_getOffers() {
        briefRentalOfferDTOList = parariusDataSource.getOffers();
        assertTrue(!briefRentalOfferDTOList.isEmpty());
        for (BriefRentalOfferDTO rentalOfferDTO : briefRentalOfferDTOList) {
            assertFalse(isNullOrEmpty(rentalOfferDTO.getAgency()));
            assertTrue(rentalOfferDTO.getArea() > 0);
            assertFalse(isNullOrEmpty(rentalOfferDTO.getPostalCode()));
            assertFalse(isNullOrEmpty(rentalOfferDTO.getLink()));
            assertFalse(isNullOrEmpty(rentalOfferDTO.getSource()));
            assertFalse(isNullOrEmpty(rentalOfferDTO.getName()));
        }
    }

    @Test
    @Order(2)
    public void test_fetchOfferDetail() {
        briefRentalOfferDTOList = parariusDataSource.getOffers();
        String url = briefRentalOfferDTOList.get(0).getLink();
        DetailRentalOffersDTO detailsDTO = parariusDataSource.fetchOfferDetail(url);
        assertNotSame(detailsDTO.getStatus(), RentalStatusDTO.DELETED);
        assertTrue(detailsDTO.getPrice() > 0);
//        assertNotNull(detailsDTO.getAvailableFrom()); //not for sure
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

*/

}
