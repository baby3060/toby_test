package com.tobexam.context;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;

import javax.sql.DataSource;


import org.springframework.core.io.ClassPathResource;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;


import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@Configuration
public class SqlServiceContext {
    @Autowired SqlMapConfig sqlMapConfig;

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setContextPath("com.tobexam.sqlconfig.jaxb");
        return unmarshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
        .setName("embeddedDatabase")
        .setType(HSQL)
        .addScript("classpath:/embsql/schema.sql")
        .addScript("classpath:/embsql/data.sql")
        .build();
    }
    
    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegstry sqlRegistry = new EmbeddedDbSqlRegstry();

        sqlRegistry.setDataSource(embeddedDatabase());

        return sqlRegistry;
    }

    @Bean
    public SqlService sqlService() {
        OxmService service = new OxmService();

        service.setUnmarshaller(unmarshaller());
        service.setSqlRegistry(sqlRegistry());
        
        return service;
    }
}