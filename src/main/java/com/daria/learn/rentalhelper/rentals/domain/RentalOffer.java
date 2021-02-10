package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Entity
@NoArgsConstructor
public class RentalOffer extends BaseEntity<Integer> {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String postalCode;
    @Getter @Setter
    private double price;
    @Getter @Setter
    private int area;
    @Getter @Setter
    private String agency;
    @Getter @Setter
    private boolean furnished;
    @Getter @Setter
    private String link;
    @Getter
    private String searchString;
    @Getter @Setter
    private Instant availableFrom;
    @Getter
    private String source;
    @Getter @Setter
    private boolean isDeleted;
    @Getter @Setter
    private RentalStatus rentalStatus;
    @OneToMany @Setter @Getter
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<OfferHistory> offerHistories;

    public RentalOffer(String name, String postalCode, int area, String agency, String link, String source) {
        this.name = name;
        this.postalCode = postalCode;
        this.area = area;
        this.agency = agency;
        this.link = link;
        this.offerHistories = List.of(new OfferHistory(Instant.now()));
        this.searchString = generateSearchString(postalCode, area, agency);
        this.source = source;
    }

    public RentalOffer(String name, String randomCode, double price, int area, String agency, boolean furnished, String link, String source) {
        this(name, randomCode, area, agency, link, source);
        this.price = price;
        this.furnished = furnished;
    }

    public static String generateSearchString(String postalCode, int area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public static String generateSearchStringFromDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return generateSearchString(briefRentalOfferDTO.getPostalCode(), briefRentalOfferDTO.getArea(), briefRentalOfferDTO.getAgency());
    }

    public static RentalOffer fromBriefRentalOfferDTO(BriefRentalOfferDTO offerDTO) {
        return new RentalOffer(offerDTO.getName(), offerDTO.getPostalCode(),
                offerDTO.getArea(), offerDTO.getAgency(),
                offerDTO.getLink(), offerDTO.getSource());
    }

    public RentalOfferDetailsDTO toRentalOfferDetailsDTO() {
        return new RentalOfferDetailsDTO(name, link, rentalStatus, postalCode, price, null, availableFrom, furnished, area, agency);
    }

    public boolean updateIfChanged(RentalOfferDetailsDTO offerDetailsDTO) {
        if (offerDetailsDTO.isDeleted() && this.isDeleted)
            return false;

        List<OfferHistory> offerChangeHistories = new LinkedList<>();
        if (offerDetailsDTO.getName() != null && !offerDetailsDTO.getName().equals(this.name)) {
            offerChangeHistories.add(new OfferHistory(Instant.now(), RentalOfferFieldNames.NAME_FIELD, offerDetailsDTO.getName(), this.name, this));
            this.name = offerDetailsDTO.getName();
        }
        if (offerDetailsDTO.getPrice() != null && offerDetailsDTO.getPrice() != this.price) {
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFieldNames.PRICE_FIELD, String.valueOf(offerDetailsDTO.getPrice()), String.valueOf(this.price), this));
            this.price = offerDetailsDTO.getPrice();
        }
        if (offerDetailsDTO.getIsFurnished() != this.furnished) {
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFieldNames.IS_FURNISHED_FIELD, String.valueOf(offerDetailsDTO.getIsFurnished()   ), String.valueOf(this.furnished), this));
            this.furnished = offerDetailsDTO.getIsFurnished();
        }
        if (offerDetailsDTO.getAvailableFrom() != null && !offerDetailsDTO.getAvailableFrom().equals(this.availableFrom)) {
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFieldNames.AVAILABLE_FROM_FIELD, String.valueOf(offerDetailsDTO.getAvailableFrom()), String.valueOf(this.availableFrom), this));
            this.availableFrom = offerDetailsDTO.getAvailableFrom();
        }
        this.offerHistories.addAll(offerChangeHistories);
        return !offerChangeHistories.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RentalOffer))
            return false;
        RentalOffer that = (RentalOffer) obj;
        return that.postalCode.equals(this.postalCode)
                && that.area == this.area
                && that.agency.equals(this.agency);
    }

    @Override
    public String toString() {
        return "RentalOffer{" +
                "id=" + id +
                ", address='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", price=" + price +
                ", area=" + area +
                ", agency='" + agency + '\'' +
                ", furnished=" + furnished +
                ", link='" + link + '\'' +
                '}';
    }
}
