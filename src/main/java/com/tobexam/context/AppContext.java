package com.tobexam.context;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;

import com.mysql.cj.jdbc.Driver;

import javax.sql.DataSource;
import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;
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

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="com.tobexam")
@ImportResource("/test-applicationContext.xml")
@Import({SqlServiceContext.class, AppContext.ProductionAppContext.class, AppContext.TestAppContext.class})
@PropertySource("/db/database.properties")
public class AppContext {

    @Value("${db.driverClass}") Class<? extends com.mysql.cj.jdbc.Driver> driverClass;
    @Value("${db.url}") String url;
    @Value("${db.username}") String username;
    @Value("${db.password}") String password;


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

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        /*
        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl(connBean.getConnStr());
        dataSource.setUsername(connBean.getUserName());
        dataSource.setPassword(connBean.getUserPass());
        */

        /*

        try {
            dataSource.setDriverClass((Class<? extends com.mysql.cj.jdbc.Driver>)Class.forName(env.getProperty("db.driverClass")));
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        */

        dataSource.setDriverClass(this.driverClass);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);

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

    @Bean
    public SqlMapConfig sqlMapConfig() {
        return new UserSqlMapConfig();
    }

}