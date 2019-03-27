package com.tobexam.context;

import com.tobexam.context.annotation.*;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;

import com.mysql.cj.jdbc.Driver;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import org.springframework.core.io.Resource;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

// 트랜잭션 매니저 추상화 인터페이스
import org.springframework.transaction.PlatformTransactionManager;
// <tx:annotation-driven /> 대용
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.mail.MailSender;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.Import;

import org.springframework.context.annotation.Profile;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="com.tobexam")
@ImportResource("/test-applicationContext.xml")
@EnableSqlService
@PropertySource("/db/database.properties")
public class AppContext implements SqlMapConfig {

    @Log Logger myLogger;

    @Value("${db.driverClass}") String driverClassName;
    @Value("${db.url}") String url;
    @Value("${db.username}") String username;
    @Value("${db.password}") String password;

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("sqlmap.xml");
    }

    @Autowired
    private Environment env;

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSender() {
            DummyMailSender mailSender = new DummyMailSender();
            mailSender.setHost("mail.server.com");
            return mailSender;
        }
    }

    @Configuration
    @Profile("test")
    public static class TestAppContext {

        @Bean
        public UserService testUserService() {
            return new UserServiceImpl.TestUserServiceImpl();
        }

        @Bean
        public MailSender mailSender() {
            DummyMailSender mailSender = new DummyMailSender();
            mailSender.setHost("mail.server.com");
            return mailSender;
        }

    }

    @Autowired
    private ConnectionBean connBean;

    @Bean
    public XMLParsingConfig xmlParsing() {
        XMLParsingConfig xmlConfig = new XMLParsingConfig();

        xmlConfig.setFileName("mysql_conn.xml");

        return xmlConfig;
    }

    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        myLogger.info("dataSource Logging");

        /*
        dataSource.setDriverClassName(connBean.getClassName());
        dataSource.setUrl(connBean.getConnStr());
        dataSource.setUsername(connBean.getUserName());
        dataSource.setPassword(connBean.getUserPass());
        */

        /*

        dataSource.setDriverClassName(env.getProperty("db.driverClass"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        */

        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);

        dataSource.setAutoCommit(false);

        return dataSource;
    }

    @Bean
    public UserService testUserService() {
        UserServiceImpl.TestUserServiceImpl service = new UserServiceImpl.TestUserServiceImpl();

        return service;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public JdbcContext jdbcContext() {
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());
        return jdbcContext;
    }


    @Bean
    public MailSender mailSender() {
        DummyMailSender mailSender = new DummyMailSender();
        mailSender.setHost("mail.server.com");
        return mailSender;
    }
}