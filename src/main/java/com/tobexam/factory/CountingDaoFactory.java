package com.tobexam.factory;

import com.tobexam.common.*;
import com.tobexam.dao.*;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class CountingDaoFactory {
    @Bean
    public UserDao userDao() {
        UserDao dao = null;

        dao = new UserDao();
        
        dao.setDataSource(dataSource());

        return dao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        ConnectionMaker realConnectionMaker = null;

        try {
            ConnectionBean mysqlConfig1 = makeConnBean();

            realConnectionMaker = new ConcreConnectionMaker(mysqlConfig1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return realConnectionMaker;
    }

    @Bean
    public ConnectionBean makeConnBean() {
        XMLParsingConfig parConfig = new XMLParsingConfig();
        parConfig.setFileName("mysql_conn.xml");
        ConnectionBean conConfig = null;
        try {
            conConfig = parConfig.setConfig();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return conConfig;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        XMLParsingConfig parConfig = new XMLParsingConfig();
        parConfig.setFileName("mysql_conn.xml");
        ConnectionBean conConfig = null;
        try {
            conConfig = parConfig.setConfig();

            String connectionStr = String.format("%s%s", conConfig.getHost(), conConfig.getDatabaseName());

            dataSource.setDriverClass((Class<? extends java.sql.Driver>)Class.forName(conConfig.getClassName()));
            dataSource.setUrl(connectionStr);
            dataSource.setUsername(conConfig.getUserName());
            dataSource.setPassword(conConfig.getUserPass());

        } catch(Exception e) {
            e.printStackTrace();
        }

        return dataSource;
    }
    

}