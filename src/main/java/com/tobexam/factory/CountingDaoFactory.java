package com.tobexam.factory;

import com.tobexam.common.*;
import com.tobexam.dao.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {
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
    

}