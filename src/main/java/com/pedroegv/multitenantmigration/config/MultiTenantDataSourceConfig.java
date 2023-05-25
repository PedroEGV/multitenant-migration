package com.pedroegv.multitenantmigration.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MultiTenantDataSourceConfig {

    @Value("${multitenant.tenant-names}")
    private List<String> schemaNames;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();

        // Set up the default data source
        multiTenantDataSource.setDefaultTargetDataSource(createDataSource("public", dataSourceProperties));

        // Set up the data sources for specific schemas
        Map<Object, Object> targetDataSources = schemaNames
                .stream()
                .collect(Collectors.toMap(Function.identity(), schemaName -> createDataSource(schemaName, dataSourceProperties)));

        multiTenantDataSource.setTargetDataSources(targetDataSources);
        multiTenantDataSource.afterPropertiesSet();

        return multiTenantDataSource;
    }

    private DataSource createDataSource(String schemaName, DataSourceProperties dataSourceProperties) {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setUrl(dataSourceProperties.determineUrl());
        pgDataSource.setUser(dataSourceProperties.determineUsername());
        pgDataSource.setPassword(dataSourceProperties.determinePassword());
        pgDataSource.setCurrentSchema(schemaName);

        return pgDataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.pedroegv.dbtest.entity1")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
