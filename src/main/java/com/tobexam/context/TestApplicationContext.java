package com.tobexam.context;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;

import com.mysql.cj.jdbc.Driver;

import javax.sql.DataSource;
import javax.annotation.Resource;

import org.springframework.core.io.ClassPathResource;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

// 트랜잭션 매니저 추상화 인터페이스
import org.springframework.transaction.PlatformTransactionManager;
// <tx:annotation-driven /> 대용
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.mail.MailSender;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@Configuration
@EnableTransactionManagement
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
    public UserDao userDao() {
        UserDaoJdbc_Template userDao = new UserDaoJdbc_Template();
        userDao.setDataSource(dataSource());
        userDao.setSqlService(sqlService());
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
        service.setSqlmap(new ClassPathResource("sqlmap.xml"));

        return service;
    }
}