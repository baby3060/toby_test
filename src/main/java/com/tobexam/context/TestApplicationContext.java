package com.tobexam.context;

import com.tobexam.common.*;

import com.mysql.cj.jdbc.Driver;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

// 트랜잭션 매니저 추상화 인터페이스
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@ImportResource("/test-applicationContext.xml")
public class TestApplicationContext {
    @Autowired
    private ConnectionBean connBean;

    @Bean
    public XMLParsingConfig xmlParsing() {
        XMLParsingConfig xmlConfig = new XMLParsingConfig();

        xmlConfig.setFileName("mysql_conn.xml");

        return xmlConfig;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl(connBean.getConnStr());
        dataSource.setUsername(connBean.getUserName());
        dataSource.setPassword(connBean.getUserPass());

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}