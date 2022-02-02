package com.daria.learn.rentalhelper.userservice.user.domain;

import com.daria.learn.rentalhelper.userservice.userpreference.domain.UserPreference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Document
@CompoundIndex(def = "{'sourceType':1, 'externalId':1}", name = "compound_index", unique = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private SourceType sourceType;

    @Getter @Setter
    private String externalId;

    @Getter @Setter
    private UserPreference userPreference;
}
