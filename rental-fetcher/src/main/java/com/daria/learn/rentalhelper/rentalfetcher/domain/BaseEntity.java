package com.daria.learn.rentalhelper.rentalfetcher.domain;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    protected ID id;
}
