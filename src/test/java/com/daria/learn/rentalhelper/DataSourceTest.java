package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.rentals.domain.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.rentals.domain.RentalStatus;
import com.daria.learn.rentalhelper.rentals.sources.DataSource;
import com.daria.learn.rentalhelper.rentals.sources.ParariusSource;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataSourceTest {

    private DataSource parariusDataSource = new ParariusSource();
    private List<BriefRentalOfferDTO> briefRentalOfferDTOList;

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
        String url = briefRentalOfferDTOList.get(0).getLink();
        RentalOfferDetailsDTO detailsDTO = parariusDataSource.fetchOfferDetail(url);
        assertNotSame(detailsDTO.getStatus(), RentalStatus.DELETED);
        assertTrue(detailsDTO.getPrice() > 0);
        assertNotNull(detailsDTO.getAvailableFrom());
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }
}
