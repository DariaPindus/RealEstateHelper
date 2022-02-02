package com.daria.learn.rentalhelper.userservice.user.repository;

import com.daria.learn.rentalhelper.userservice.user.domain.SourceType;
import com.daria.learn.rentalhelper.userservice.user.domain.User;
import com.daria.learn.rentalhelper.userservice.user.domain.projection.UserPreferenceUserProjection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {

    Optional<UserPreferenceUserProjection> findUserPreferenceBySourceTypeAndExternalId(SourceType sourceType, String externalId);

    Optional<User> findBySourceTypeAndExternalId(SourceType sourceType, String externalId);

    @Query(value = "{sourceType: ?0, {" +
            "$or: [{userPreference.price : {$lte : ?1}}, {userPreference.price: null}]}," +
            "$or: [{userPreference.minArea : {$gte : ?2}}, {userPreference.minArea: null}]}," +
            "$or: [{userPreference.furnished : ?3}, {userPreference.furnished: null}]}" +
            " }")
    //TODO: check with nulls
    List<User> findAllByMatchingPreferenceAndSourceType(SourceType sourceType, Double price, Integer minArea, Boolean furnished);

}
