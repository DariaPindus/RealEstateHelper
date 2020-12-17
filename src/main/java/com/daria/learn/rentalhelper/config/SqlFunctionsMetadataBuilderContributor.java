package com.daria.learn.rentalhelper.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

//Didn't work :(
//https://vladmihalcea.com/hibernate-sql-function-jpql-criteria-api-query/
public class SqlFunctionsMetadataBuilderContributor implements MetadataBuilderContributor {
    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
                "convert_to_int",
                new StandardSQLFunction(
                        "convert(?1, unsigned)",
                        StandardBasicTypes.INTEGER
                )
        );
    }
}
