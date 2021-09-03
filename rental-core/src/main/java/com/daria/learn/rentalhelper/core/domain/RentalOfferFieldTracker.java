package com.daria.learn.rentalhelper.core.domain;

import com.daria.learn.rentalhelper.core.persist.RentalOfferFacade;
import com.daria.learn.rentalhelper.dtos.DetailRentalOffersDTO;
import com.daria.learn.rentalhelper.dtos.RentalStatusDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO: finish me
@Component
public class RentalOfferFieldTracker implements FieldTracker<RentalOffer> {

    private final List<FieldChangeTransformer<RentalOffer, ?>> fieldChangeTransformers = List.of(new StatusFieldChangeTransformer());

    @Override
    public List<FieldChange> getFieldChanges(RentalOffer currentEntity, RentalOffer newEntity) {
        return fieldChangeTransformers.stream()
                .filter(transformer -> transformer.wasChanged(currentEntity, newEntity))
                .map(transformer -> transformer.getFieldChange(currentEntity, newEntity))
                .collect(Collectors.toUnmodifiableList());
    }

    interface FieldChangeTransformer<E, F> {
        FieldChange getFieldChange(E currentValue, E newValue);
        F getFieldFromEntity(E entity);
        default boolean wasChanged(E currentValue, E newValue) {
            return Objects.equals(currentValue, newValue);
        }
    }

    class StatusFieldChangeTransformer implements FieldChangeTransformer<RentalOffer, RentalStatus> {

        @Override
        public FieldChange getFieldChange(RentalOffer currentValue, RentalOffer newValue) {
            String currentValueStr = Optional.ofNullable(getFieldFromEntity(currentValue)).map(RentalStatus::getValue).orElse(FieldChange.NULL_VALUE);
            String newValueStr = Optional.ofNullable(getFieldFromEntity(currentValue)).map(RentalStatus::getValue).orElse(FieldChange.NULL_VALUE);
            return new FieldChange(RentalOfferFields.RENTAL_STATUS_FIELD, currentValueStr, newValueStr);
        }

        @Override
        public RentalStatus getFieldFromEntity(RentalOffer rentalOffer) {
            return rentalOffer.getRentalStatus();
        }

    }

    class PriceFieldChangeTransformer implements FieldChangeTransformer<RentalOffer, Double> {

        @Override
        public FieldChange getFieldChange(RentalOffer currentValue, RentalOffer newValue) {
            String currentValueStr = Optional.ofNullable(getFieldFromEntity(currentValue)).map(String::valueOf).orElse(FieldChange.NULL_VALUE);
            String newValueStr = Optional.ofNullable(getFieldFromEntity(currentValue)).map(String::valueOf).orElse(FieldChange.NULL_VALUE);
            return new FieldChange(RentalOfferFields.RENTAL_STATUS_FIELD, currentValueStr, newValueStr);
        }

        @Override
        public Double getFieldFromEntity(RentalOffer entity) {
            return entity.getPrice();
        }
    }


}
