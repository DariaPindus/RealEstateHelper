package com.daria.learn.rentalhelper.rentals.persist;


public interface RentalOfferQueries {
    String FIND_BY_SEARCH_STRING_IN = "rentalOffer_findBySearchStringIn";
    String FIND_BY_SEARCH_STRING_IN_QUERY = "select ro from RentalOffer ro where ro.searchString in ?1";

    String FIND_BY_NAME = "rentalOffer_findByName";
    String FIND_BY_NAME_QUERY = "select distinct ro from RentalOffer ro join fetch ro.offerHistories where ro.name=?1";

    String FIND_BY_NAME_CONTAINS = "rentalOffer_findByNameContains";
    String FIND_BY_NAME_CONTAINS_QUERY = "select distinct ro from RentalOffer ro join fetch ro.offerHistories where ro.name like CONCAT('%', ?1,'%')";

    String FIND_BY_AGENCIES_PAGED = "rentalOffer_findAllByAgencyPaged";
    String FIND_BY_AGENCIES_PAGED_QUERY = "select ro from RentalOffer ro where ro.agency=?1";

    String FIND_BY_PRICE_GREATER_AND_AREA_LESS = "rentalOffer_findAllByPriceGreaterThanAndAreaLessThan";
    String FIND_BY_PRICE_GREATER_AND_AREA_LESS_QUERY = "select ro from RentalOffer ro where ro.price > ?1 and ro.area < ?2";

    String FIND_ALL_UPDATED_AFTER = "rentalOffer_findAllUpdatedAfter";
    String FIND_ALL_UPDATED_AFTER_QUERY = "select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.time >= ?1 and oh.status=?2";

    String FIND_ALL_UPDATED_BY_FIELD = "rentalOffer_findAllUpdatedByFieldName";
    String FIND_ALL_UPDATED_BY_FIELD_QUERY = "select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.fieldHistory.fieldName = ?1";

    String FIND_ALL_PRICE_GREW = "rentalOffer_findAllPriceGrewUp";
    String FIND_ALL_PRICE_GREW_QUERY = "select distinct ro from RentalOffer ro join fetch ro.offerHistories oh where oh.time >= ?1";

    String COUNT_ALL = "rentalOffer_countAll";
    String COUNT_ALL_QUERY = "select count(ro) from RentalOffer ro";

    String COUNT_CREATED_LAST_MONTH = "rentalOffer_countCreatedInLastMonth";
    String COUNT_CREATED_LAST_MONTH_QUERY = "select count(ro) from RentalOffer ro where exists (select 1 from ro.offerHistories oh " +
            "where oh.rentalOffer.id=ro.id and oh.time>=?1 and oh.status=?2)";

    String FIND_ALL_UPDATED_AFTER_ORDERED = "rentalOffer_findAllUpdatedAfterOrdered";
    String FIND_ALL_UPDATED_AFTER_ORDERED_QUERY = "select new org.apache.commons.lang3.tuple.ImmutablePair(ro, oh) from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1 order by oh.time desc"; //throws query specified join fetching, but the owner of the fetched association was not present in the select list

    String GET_AGENCY_WITH_MOST_OFFERS_IN_30_DAYS = "rentalOffer_getAgencyWithMostOffersLast30Days";
    String GET_AGENCY_WITH_MOST_OFFERS_IN_30_DAYS_QUERY = "select new org.apache.commons.lang3.tuple.ImmutablePair(ro.agency, count(oh.id)) from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1 group by ro.agency order by count(oh.id) desc";

}
