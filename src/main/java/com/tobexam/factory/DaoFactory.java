package com.tobexam.factory;

import com.tobexam.common.*;
import com.tobexam.dao.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        UserDao dao = null;

        dao = new UserDao();
        dao.setConnectionMaker(connectionMaker());

        // dao = new UserDao(connectionMaker());

        return dao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        XMLParsingConfig parConfig = new XMLParsingConfig();
        ConnectionMaker connectionMaker = null;

        try {
            ConnectionBean mysqlConfig1 = makeConnBean("mysql_conn.xml");

            connectionMaker = new ConcreConnectionMaker(mysqlConfig1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return connectionMaker;
    }

    @Bean
    public ConnectionBean makeConnBean(String fileName) {
        XMLParsingConfig parConfig = new XMLParsingConfig();
        ConnectionBean conConfig = null;
        try {
            conConfig = parConfig.setConfig(fileName);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return conConfig;
    }

}