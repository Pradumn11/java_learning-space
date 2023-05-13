package com.tech.learningspace.Utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LearningSpaceModule {


    @Bean
    @Primary
    public BasicDataSource createDatasource(@Value("${postgres.jdbc.url}") String url,
                                            @Value("${postgres.jdbc.username}") String userName,
                                            @Value("${postgres.jdbc.password}") String password,
                                            @Value("${spring.database.driverClassName}")String driver
    ){

        BasicDataSource dataSource=new BasicDataSource();
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        return dataSource;

    }


}
