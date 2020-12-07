package com.daria.learn.rentalhelper.performance;

import lombok.Getter;

import java.util.Set;

public class OrmMethodConfig {
    private final Set<String> unsupportedMethods;

    public OrmMethodConfig() {
        this(Set.of());
    }

    public OrmMethodConfig(Set<String> unsupportedMethods) {
        this.unsupportedMethods = unsupportedMethods;
    }

    public boolean isSupported(String method) {
        return !unsupportedMethods.contains(method);
    }

}
