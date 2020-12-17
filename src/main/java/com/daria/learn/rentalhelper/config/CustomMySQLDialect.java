package com.daria.learn.rentalhelper.config;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQLDialect extends MySQL8Dialect {
    public CustomMySQLDialect() {
        super();
        registerFunction("convert_to_int", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "convert(?1, unsigned)"));
    }
}