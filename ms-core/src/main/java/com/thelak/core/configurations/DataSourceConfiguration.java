package com.thelak.core.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.datasource.driverClassName}")
    String driverClassName;

    @Value("${spring.datasource.maximumPoolSize:20}")
    Integer maximumPoolSize;

    @Value("${spring.datasource.minimumIdle:5}")
    Integer minimumIdle;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("default-data-source");
        jdbcConfig.setMaximumPoolSize(maximumPoolSize);
        jdbcConfig.setMinimumIdle(minimumIdle);
        jdbcConfig.setJdbcUrl(url);
        jdbcConfig.setUsername(username);
        jdbcConfig.setPassword(password);
        jdbcConfig.setDriverClassName(driverClassName);

        return new HikariDataSource(jdbcConfig);
    }
}
