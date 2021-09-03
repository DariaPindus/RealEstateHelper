package com.daria.learn.rentalhelper.core.domain;

import com.daria.learn.rentalhelper.dtos.BriefRentalOfferDTO;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(indexes = @Index(columnList = "link"))
@DiscriminatorColumn(name = "source")
public class RentalOffer extends BaseEntity<Integer> {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String postalCode;
    @Getter @Setter
    private Double price;
    @Getter @Setter
    private Integer area;
    @Getter @Setter
    private String agency;
    @Getter @Setter
    //TODO: nullable?
    private Boolean furnished;
    @Getter @Setter
    @Column(unique = true)
    private String link;
    @Getter
    private String searchString;
    @Getter @Setter
    private Instant availableFrom;
    @Getter
    @Column(name = "source", insertable = false, updatable = false)
    private String source;
    @Getter @Setter
    private RentalStatus rentalStatus;
    @OneToMany @Setter @Getter
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<FieldChange> offerHistories;
    @Getter @Setter
    private Instant creationTime;
    @Transient
    private boolean shouldBeNotifiedAbout;

    public RentalOffer(String name, String postalCode, Integer area, Double price, String agency, String link, String source) {
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

    public RentalOffer(String name, String postalCode, Double price, Integer area, String agency, Boolean furnished, String link, String source) {
        this(name, postalCode, area, price, agency, link, source);
        this.furnished = furnished;
    }

    public boolean shouldBeNotifiedAbout() {
        return shouldBeNotifiedAbout;
    }

    public static String generateSearchString(String postalCode, Integer area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public static String generateSearchStringFromDTO(BriefRentalOfferDTO briefRentalOfferDTO) {
        return generateSearchString(briefRentalOfferDTO.getPostalCode(), briefRentalOfferDTO.getArea(), briefRentalOfferDTO.getAgency());
    }

    public static RentalOffer fromBriefRentalOfferDTO(BriefRentalOfferDTO offerDTO) {
        if (offerDTO.getSource().equals(ParariusRentalOffer.DISCRIMINATOR_VALUE))
            return ParariusRentalOffer.fromBriefRentalOfferDTO(offerDTO);
        else
            throw new IllegalArgumentException("Cannot create RentalOffer of source " + offerDTO.getSource());
    }

    public DetailRentalOffersDTO toRentalOfferDetailsDTO() {
        return new DetailRentalOffersDTO(name, link, RentalStatusDTO.fromValue(rentalStatus.getValue()), postalCode, price, null, availableFrom, furnished, area, agency);
    }

    //TODO: refactor to enhance "shouldBeNotifiedAbout" logic
    //TODO: enhance field tracking, maybe create class Field that would implement Trackable interface, that would override getOldValue/getNewValue
    public boolean updateFromDetails(DetailRentalOffersDTO offerDetailsDTO) {
        List<FieldChange> offerChangeHistories = new LinkedList<>();
        shouldBeNotifiedAbout = this.offerHistories.isEmpty();

        RentalStatus newRentalStatus = Optional.ofNullable(offerDetailsDTO.getStatus()).map(RentalStatusDTO::getValue).map(RentalStatus::fromValue).orElse(RentalStatus.OTHER);
        if (offerDetailsDTO.getStatus() != null && newRentalStatus != this.rentalStatus) {
            String oldValue = this.rentalStatus == null ? FieldChange.NULL_VALUE : this.rentalStatus.getValue();
            if (offerDetailsDTO.isDeleted() && this.rentalStatus == RentalStatus.DELETED)
                return false;
            offerChangeHistories.add(new FieldChange(RentalOfferFields.RENTAL_STATUS_FIELD, offerDetailsDTO.getStatus().getValue(), oldValue, this));
            this.rentalStatus = newRentalStatus;
        }
        if (offerDetailsDTO.getName() != null && !offerDetailsDTO.getName().equals(this.name)) {
            offerChangeHistories.add(new FieldChange(RentalOfferFields.NAME_FIELD, offerDetailsDTO.getName(), this.name, this));
            this.name = offerDetailsDTO.getName();
        }
        if (offerDetailsDTO.getPrice() != null && offerDetailsDTO.getPrice() != this.price) {
            shouldBeNotifiedAbout = true;
            String oldValue = this.price == null ? FieldChange.NULL_VALUE : String.valueOf(this.price);
            offerChangeHistories.add(
                    new FieldChange(RentalOfferFields.PRICE_FIELD, offerDetailsDTO.getPrice() == null ? FieldChange.NULL_VALUE : String.valueOf(offerDetailsDTO.getPrice()), oldValue, this));
            this.price = offerDetailsDTO.getPrice();
        }
        if (offerDetailsDTO.getIsFurnished() != null && offerDetailsDTO.getIsFurnished() != this.furnished) {
            offerChangeHistories.add(
                    new FieldChange(RentalOfferFields.IS_FURNISHED_FIELD, offerDetailsDTO.getIsFurnished() == null ? FieldChange.NULL_VALUE : String.valueOf(offerDetailsDTO.getIsFurnished() ), String.valueOf(this.furnished), this));
            this.furnished = offerDetailsDTO.getIsFurnished();
        }
        if (offerDetailsDTO.getAvailableFrom() != null && !offerDetailsDTO.getAvailableFrom().equals(this.availableFrom)) {
            shouldBeNotifiedAbout = true;
            offerChangeHistories.add(
                    new FieldChange(RentalOfferFields.AVAILABLE_FROM_FIELD, String.valueOf(offerDetailsDTO.getAvailableFrom()), String.valueOf(this.availableFrom), this));
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
