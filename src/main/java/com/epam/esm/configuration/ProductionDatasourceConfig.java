package com.epam.esm.configuration;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:database.properties")
@Profile("production")
public class ProductionDatasourceConfig {

    @Value("${db.url}")
    private String url;
    @Value("${db.driver}")
    private String driverClassName;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;
    @Value("${db.initialSize}")
    private int initialSize;
    @Value("${db.maxActive}")
    private int maxActive;

    @Bean
    @Profile("production") // todo: repeated profile?
    @Scope("singleton")
    public DataSource mysqlDataSource() {

        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName(driverClassName);
        p.setUsername(username);
        p.setPassword(password);
        p.setInitialSize(initialSize);
        p.setMaxActive(maxActive);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
        datasource.setPoolProperties(p);

        return datasource;
    }

}
