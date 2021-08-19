package com.daria.learn.rentalhelper.core.stubs;

import com.daria.learn.rentalhelper.core.domain.BaseEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toList;

public abstract class InMemoryRepository<T extends BaseEntity<ID>, ID extends Serializable> implements CrudRepository<T, ID> {
    protected Map<ID, T> storage = new HashMap<>();

    @Override
    public <S extends T> S save(S s) {
        storage.put(s.getId(), s);
        return s;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> iterable) {
        iterable.forEach(this::save);
        return iterable;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        Set<ID> ids = iterableToSet(iterable);

        return storage.entrySet().stream().filter(ids::contains).map(Map.Entry::getValue).collect(toList());
    }

    @NotNull
    private <V> Set<V> iterableToSet(Iterable<V> iterable) {
        Set<V> set = new HashSet<>();
        iterable.forEach(set::add);
        return set;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteById(ID id) {
        storage.remove(id);
    }

    @Override
    public void delete(T t) {
        Optional<ID> id = storage.values().stream().filter(val -> val.equals(t)).findFirst().map(T::getId);
        id.ifPresent(value -> storage.remove(value));
    }

    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        Set<? extends T> values = iterableToSet(iterable);
        List<ID> ids = storage.values().stream().filter(values::contains).map(BaseEntity::getId).collect(toList());
        ids.forEach(storage::remove);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
