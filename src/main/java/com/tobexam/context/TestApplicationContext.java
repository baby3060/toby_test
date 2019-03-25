package com.tobexam.context;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;

import com.mysql.cj.jdbc.Driver;

import javax.sql.DataSource;


import org.springframework.mail.MailSender;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

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

    @Autowired
    private SqlService sqlService;

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
    public UserDao userDao() {
        UserDaoJdbc_Template userDao = new UserDaoJdbc_Template();
        userDao.setDataSource(dataSource());
        userDao.setSqlService(this.sqlService);
        return userDao;
    }

    @Bean
    public UserService userService() {
        UserServiceImpl service = new UserServiceImpl();

        service.setUserDao(userDao());
        service.setMailSender(mailSender());

        return service;
    }

    @Bean
    public UserService testUserService() {
        UserServiceImpl.TestUserServiceImpl service = new UserServiceImpl.TestUserServiceImpl();

        service.setUserDao(userDao());
        service.setMailSender(mailSender());

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
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setContextPath("com.tobexam.sqlconfig.jaxb");
        return unmarshaller;
    }

    @Bean
    public MailSender mailSender() {
        DummyMailSender mailSender = new DummyMailSender();
        mailSender.setHost("mail.server.com");
        return mailSender;
    }
}