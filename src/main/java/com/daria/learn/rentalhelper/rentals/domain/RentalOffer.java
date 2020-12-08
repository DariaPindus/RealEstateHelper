package com.daria.learn.rentalhelper.rentals.domain;

import com.daria.learn.rentalhelper.rentals.persist.RentalOfferQueries;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@NamedQuery(
        name = "RentalOffer_FindBySearchableFields",
        query = "select ro from RentalOffer ro " +
        "where ro.name=:address and " +
        "ro.agency=:agency and " +
        "ro.postalCode=:postalCode"
)
@NamedQueries(value = {
        @NamedQuery(name = RentalOfferQueries.COUNT_ALL, query = RentalOfferQueries.COUNT_ALL_QUERY),
        @NamedQuery(name = RentalOfferQueries.COUNT_CREATED_LAST_MONTH, query = RentalOfferQueries.COUNT_CREATED_LAST_MONTH_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_ALL_PRICE_GREW, query = RentalOfferQueries.FIND_ALL_PRICE_GREW_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_BY_NAME, query = RentalOfferQueries.FIND_BY_NAME_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_ALL_UPDATED_AFTER, query = RentalOfferQueries.FIND_ALL_UPDATED_AFTER_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_ALL_UPDATED_AFTER_ORDERED, query = RentalOfferQueries.FIND_ALL_UPDATED_AFTER_ORDERED_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_BY_AGENCIES_PAGED, query = RentalOfferQueries.FIND_BY_AGENCIES_PAGED_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_BY_PRICE_GREATER_AND_AREA_LESS, query = RentalOfferQueries.FIND_BY_PRICE_GREATER_AND_AREA_LESS_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_BY_NAME_CONTAINS, query = RentalOfferQueries.FIND_BY_NAME_CONTAINS_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_ALL_UPDATED_BY_FIELD, query = RentalOfferQueries.FIND_ALL_UPDATED_BY_FIELD_QUERY),
        @NamedQuery(name = RentalOfferQueries.FIND_BY_SEARCH_STRING_IN, query = RentalOfferQueries.FIND_BY_SEARCH_STRING_IN_QUERY),
})
public class RentalOffer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    @Column(unique = true)
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
        this.offerHistories = List.of(new OfferHistory(Instant.now(), OfferStatus.NEW, null, this));
        this.searchString = generateSearchString(postalCode, area, agency);
    }

    public static String generateSearchString(String postalCode, int area, String agency) {
        return postalCode + "--" + area + "--" + agency;
    }

    public static RentalOffer fromRentalOfferDTO(RentalOfferDTO offerDTO) {
        return new RentalOffer(offerDTO.getName(), offerDTO.getPostalCode(),
                offerDTO.getPrice(), offerDTO.getArea(), offerDTO.getAgency(),
                offerDTO.isFurnished(), offerDTO.getLink());
    }

    public boolean updateIfChanged(RentalOffer newRentalOffer) {
        boolean wasChanged = addOfferHistoryIfChanged(newRentalOffer);
        this.name = newRentalOffer.getName();
        this.price = newRentalOffer.getPrice();
        this.link = newRentalOffer.getLink();
        this.furnished = newRentalOffer.isFurnished();
        return wasChanged;
    }

    private boolean addOfferHistoryIfChanged(RentalOffer newOfferVersion) {
        List<FieldHistory> fieldHistories = new LinkedList<>();

        if (newOfferVersion.getPrice() != this.price)
            fieldHistories.add(new FieldHistory("price", String.valueOf(newOfferVersion.price), String.valueOf(this.price)));
        if (!newOfferVersion.getLink().equals(this.link))
            fieldHistories.add(new FieldHistory("link", String.valueOf(newOfferVersion.link), String.valueOf(this.link)));
        if (newOfferVersion.getArea() != this.area)
            fieldHistories.add(new FieldHistory("area", String.valueOf(newOfferVersion.area), String.valueOf(this.area)));

        if (!fieldHistories.isEmpty()) {
            this.offerHistories.addAll(fieldHistories.stream().map(fieldHistory -> new OfferHistory(Instant.now(), OfferStatus.UPDATED, fieldHistory, this)).collect(Collectors.toList()));
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
