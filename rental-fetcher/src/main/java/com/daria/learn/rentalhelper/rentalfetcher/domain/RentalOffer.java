package com.daria.learn.rentalhelper.rentalfetcher.domain;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.RentalOfferDetailsDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(indexes = @Index(columnList = "link"))
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
    //TODO: nullable?
    private boolean furnished;
    @Getter @Setter
    @Column(unique = true)
    private String link;
    @Getter
    private String searchString;
    @Getter @Setter
    private Instant availableFrom;
    @Getter
    private String source;
    @Getter @Setter
    private RentalStatus rentalStatus;
    @OneToMany @Setter @Getter
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<OfferHistory> offerHistories;
    @Getter @Setter
    private Instant creationTime;
    @Transient
    private boolean shouldBeNotifiedAbout;

    public RentalOffer(String name, String postalCode, int area, double price, String agency, String link, String source) {
        this.name = name;
        this.postalCode = postalCode;
        this.price = price;
        this.area = area;
        this.agency = agency;
        this.link = link;
        this.offerHistories = new LinkedList<>();
        this.searchString = generateSearchString(postalCode, area, agency);
        this.source = source;
        this.rentalStatus = RentalStatus.AVAILABLE;
        this.creationTime = Instant.now();
    }

    public RentalOffer(String name, String postalCode, double price, int area, String agency, boolean furnished, String link, String source) {
        this(name, postalCode, area, price, agency, link, source);
        this.furnished = furnished;
    }

    public boolean shouldBeNotifiedAbout() {
        return shouldBeNotifiedAbout;
    }

    public static String generateSearchString(String postalCode, int area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public static String generateSearchStringFromDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return generateSearchString(briefRentalOfferDTO.getPostalCode(), briefRentalOfferDTO.getArea(), briefRentalOfferDTO.getAgency());
    }

    public static RentalOffer fromBriefRentalOfferDTO(BriefRentalOfferDTO offerDTO) {
        return new RentalOffer(offerDTO.getName(), offerDTO.getPostalCode(),
                offerDTO.getArea(), offerDTO.getPrice(), offerDTO.getAgency(),
                offerDTO.getLink(), offerDTO.getSource());
    }

    public BriefRentalOfferDTO toBriefRentalOfferDTO() {
        return new BriefRentalOfferDTO(name, postalCode, area, agency, link, price, source);
    }

    public RentalOfferDetailsDTO toRentalOfferDetailsDTO() {
        return new RentalOfferDetailsDTO(name, link, RentalStatusDTO.fromValue(rentalStatus.getValue()), postalCode, price, null, availableFrom, furnished, area, agency);
    }

    public boolean updateFromDetails(RentalOfferDetailsDTO offerDetailsDTO) {
        List<OfferHistory> offerChangeHistories = new LinkedList<>();
        shouldBeNotifiedAbout = this.offerHistories.isEmpty();

        RentalStatus newRentalStatus = RentalStatus.fromValue(offerDetailsDTO.getStatus().getValue());
        if (offerDetailsDTO.getStatus() != null && newRentalStatus != this.rentalStatus) {
            if (offerDetailsDTO.isDeleted() && this.rentalStatus == RentalStatus.DELETED)
                return false;
            offerChangeHistories.add(new OfferHistory(Instant.now(), RentalOfferFields.RENTAL_STATUS_FIELD, offerDetailsDTO.getStatus().getValue(), this.rentalStatus.getValue(), this));
            this.rentalStatus = newRentalStatus;
        }
        if (offerDetailsDTO.getName() != null && !offerDetailsDTO.getName().equals(this.name)) {
            offerChangeHistories.add(new OfferHistory(Instant.now(), RentalOfferFields.NAME_FIELD, offerDetailsDTO.getName(), this.name, this));
            this.name = offerDetailsDTO.getName();
        }
        if (offerDetailsDTO.getPrice() != null && offerDetailsDTO.getPrice() != this.price) {
            shouldBeNotifiedAbout = true;
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFields.PRICE_FIELD, String.valueOf(offerDetailsDTO.getPrice()), String.valueOf(this.price), this));
            this.price = offerDetailsDTO.getPrice();
        }
        if (offerDetailsDTO.getIsFurnished() != null && offerDetailsDTO.getIsFurnished() != this.furnished) {
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFields.IS_FURNISHED_FIELD, String.valueOf(offerDetailsDTO.getIsFurnished() ), String.valueOf(this.furnished), this));
            this.furnished = offerDetailsDTO.getIsFurnished();
        }
        if (offerDetailsDTO.getAvailableFrom() != null && !offerDetailsDTO.getAvailableFrom().equals(this.availableFrom)) {
            shouldBeNotifiedAbout = true;
            offerChangeHistories.add(
                    new OfferHistory(Instant.now(), RentalOfferFields.AVAILABLE_FROM_FIELD, String.valueOf(offerDetailsDTO.getAvailableFrom()), String.valueOf(this.availableFrom), this));
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
