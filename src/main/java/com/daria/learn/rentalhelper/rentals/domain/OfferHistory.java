package com.daria.learn.rentalhelper.rentals.domain;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class OfferHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    private Integer id;
    @Getter @Setter
    private Instant time;
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private OfferStatus status;
    @Embedded
    @Getter @Setter
    @Nullable
    private FieldHistory fieldHistory;

    public OfferHistory() {
    }

    public OfferHistory(Instant time) {
        this.time = time;
        this.status = OfferStatus.NEW;
        this.fieldHistory = null;
    }

    public OfferHistory(Instant time, OfferStatus offerStatus, FieldHistory history) {
        this.time = time;
        this.status = offerStatus;
        this.fieldHistory = history;
    }
}
