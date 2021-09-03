package com.daria.learn.rentalhelper.core.domain;

import java.util.List;

public interface FieldTracker<T> {

    List<FieldChange> getFieldChanges(T currentEntity, T newEntity);

}
