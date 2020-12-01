package com.daria.learn.rentalhelper.rentals.persist;


import javax.persistence.NamedQuery;

@NamedQuery(name = "rentalOffer_findBySearchStringIn", query = "select ro from RentalOffer ro where ro.searchString in ?1")
@NamedQuery(name = "rentalOffer_findByName", query = "select ro, ro.offerHistories from RentalOffer ro where ro.name=?1")
@NamedQuery(name = "rentalOffer_findByNameContains", query = "select ro, ro.offerHistories from RentalOffer ro where ro.name like CONCAT('%', ?1,'%')")
@NamedQuery(name = "rentalOffer_findAllByAgencyPaged", query = "select ro, ro.offerHistories from RentalOffer ro where ro.agency=?1")
@NamedQuery(name = "rentalOffer_findAllByPriceGreaterThanAndAreaLessThan", query = "select ro from RentalOffer ro where ro.price > ?2 and ro.area < ?3")
@NamedQuery(name = "rentalOffer_findAllUpdatedAfter", query = "select ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1")
@NamedQuery(name = "rentalOffer_findAllUpdatedByFieldName", query = "select ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.fieldHistory.fieldName = ?1")
@NamedQuery(name = "rentalOffer_findAllPriceGrewUp", query = "select distinct ro, ro.offerHistories from RentalOffer ro join ro.offerHistories oh where oh.time >= ?1")
@NamedQuery(name = "rentalOffer_countAll", query = "select count(ro) from RentalOffer ro")
@NamedQuery(name = "rentalOffer_countCreatedInLastMonth", query = "select count(ro) from RentalOffer ro where exists (select 1 from ro.offerHistories oh where oh.rentalOffer.id=ro.id and oh.time>=?1)")
public @interface RentalOfferQueries {
}
