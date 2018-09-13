package org.vkomlev.jwt.authority.server.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DBConfiguration {
    @Value("classpath:schema.sql")
    private Resource schemaScript;

    @Value("${jwt.jdbc.url}")
    private String jdbcUrl;

    @Value("${jwt.jdbc.user}")
    private String jdbcUser;

    @Value("${jwt.jdbc.password}")
    private String jdbcPassword;

    @Value("${jwt.jdbc.driver")
    private String driverName;

//    @Bean(name = "dataSource")
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverName);
//        dataSource.setUrl(jdbcUrl);
//        dataSource.setUsername(jdbcUser);
//        dataSource.setPassword(jdbcPassword);
//        return dataSource;
//    }
//
//    @Bean
//    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
//        DataSourceInitializer initializer = new DataSourceInitializer();
//        initializer.setDataSource(dataSource);
//        initializer.setDatabasePopulator(databasePopulator());
//        return initializer;
//    }
//
//    private DatabasePopulator databasePopulator() {
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.addScript(schemaScript);
//        return populator;
//    }
}
