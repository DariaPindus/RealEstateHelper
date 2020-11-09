package com.daria.learn.rentalhelper.rentals.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@NoArgsConstructor
public class RentalOffer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @OneToMany @Setter @Getter
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<OfferHistory> offerHistories;
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

    public RentalOffer(String name, String postalCode, double price, int area, String agency, boolean furnished, String link) {
        this.name = name;
        this.postalCode = postalCode;
        this.price = price;
        this.area = area;
        this.agency = agency;
        this.furnished = furnished;
        this.link = link;
        this.offerHistories = List.of(new OfferHistory(Instant.now(), OfferStatus.NEW, null));
        this.searchString = generateSearchString(postalCode, area, agency);
    }

    public static String generateSearchString(String postalCode, int area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public boolean addOfferHistoryIfNeeded(RentalOffer newOfferVersion) {
        FieldHistory fieldHistory = null;

        if (newOfferVersion.getPrice() != this.price)
            fieldHistory = new FieldHistory("price", String.valueOf(this.price), String.valueOf(newOfferVersion.price));
        if (!newOfferVersion.getLink().equals(this.link))
            fieldHistory = new FieldHistory("link", String.valueOf(this.link), String.valueOf(newOfferVersion.link));
        if (newOfferVersion.getArea() != this.area)
            fieldHistory = new FieldHistory("area", String.valueOf(this.area), String.valueOf(newOfferVersion.area));

        if (fieldHistory != null) {
            this.offerHistories.add(new OfferHistory(Instant.now(), OfferStatus.UPDATED, fieldHistory));
            return true;
        }
        return false;
    }

    public static RentalOffer fromRentalOfferDTO(RentalOfferDTO offerDTO) {
        return new RentalOffer(offerDTO.getName(), offerDTO.getPostalCode(),
                offerDTO.getPrice(), offerDTO.getArea(), offerDTO.getAgency(),
                offerDTO.isFurnished(), offerDTO.getLink());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RentalOffer))
            return false;
        RentalOffer that = (RentalOffer) obj;
        return that.postalCode.equals(this.postalCode)
                && that.name.equals(this.name)
                && that.area == this.area;
    }

    @Override
    public String toString() {
        return "RentalOffer{" +
                "id=" + id +
                ", offerHistories=" + offerHistories +
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
