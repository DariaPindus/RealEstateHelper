package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@MappedSuperclass
public class RentalOffer extends BaseEntity {
    @OneToMany @Setter @Getter
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<FieldHistory> offerHistories;
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
    private final String searchString;
    @Getter @Setter
    private Instant availableFrom;
    @Getter
    private final String source;
    @Getter @Setter
    private boolean isDeleted;

    public RentalOffer(String name, String postalCode, double price, int area, String agency, boolean furnished, String link, String source) {
        this(name, postalCode, area, agency, link, source);
        this.price = price;
        this.furnished = furnished;
    }

    public RentalOffer(String name, String postalCode, int area, String agency, String link, String source) {
        this.name = name;
        this.postalCode = postalCode;
        this.area = area;
        this.agency = agency;
        this.link = link;
        this.offerHistories = List.of(new FieldHistory(Instant.now(), OfferStatus.NEW, null));
        this.searchString = generateSearchString(postalCode, area, agency);
        this.source = source;
    }

    public static String generateSearchString(String postalCode, int area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public static String generateSearchStringFromDTO(RentalOfferDTO rentalOfferDTO) {
        return generateSearchString(rentalOfferDTO.getPostalCode(), rentalOfferDTO.getArea(), rentalOfferDTO.getAgency());
    }

    public static RentalOffer fromRentalOfferDTO(RentalOfferDTO offerDTO) {
        return new RentalOffer(offerDTO.getName(), offerDTO.getPostalCode(),
                offerDTO.getArea(), offerDTO.getAgency(),
                offerDTO.getLink(), offerDTO.getSource());
    }

    public boolean updateIfChanged(RentalOfferDetailsDTO offerDetailsDTO) {
        if (offerDetailsDTO.isDeleted())
            return !this.isDeleted; //returns flag that it was changed if it wasn't already deleted

        boolean wasChanged = addOfferHistoryIfChanged(offerDetailsDTO);
        this.name = offerDetailsDTO.getName();
        this.price = offerDetailsDTO.getPrice();
        this.availableFrom = offerDetailsDTO.getAvailableFrom();

        if (offerDetailsDTO.getIsFurnished() != null)
            this.furnished = offerDetailsDTO.getIsFurnished();

        return wasChanged;
    }

    private boolean addOfferHistoryIfChanged(RentalOfferDetailsDTO newOfferDetailsDTO) {
        List<FieldHistory> fieldHistories = new LinkedList<>();

        if (newOfferDetailsDTO.getPrice() != this.price)
            fieldHistories.add(new FieldHistory("price", String.valueOf(this.price), String.valueOf(newOfferDetailsDTO.getPrice())));
        if (newOfferDetailsDTO.getArea() != this.area)
            fieldHistories.add(new FieldHistory("area", String.valueOf(this.area), String.valueOf(newOfferDetailsDTO.getArea())));


        if (!fieldHistories.isEmpty()) {
            this.offerHistories.addAll(fieldHistories.stream().map(fieldHistory -> new FieldHistory(Instant.now(), OfferStatus.UPDATED, fieldHistory)).collect(Collectors.toList()));
            return true;
        }
        return false;
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
