package com.tobexam.factory;

import com.tobexam.common.*;
import com.tobexam.dao.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
    public UserDao userDao() {
        UserDao dao = null;

        dao = new UserDao();
        dao.setConnectionMaker(connectionMaker());

        // dao = new UserDao(connectionMaker());

        return dao;
    }

    public ConnectionMaker connectionMaker() {
        XMLParsingConfig parConfig = new XMLParsingConfig();
        ConnectionMaker connectionMaker = null;

        try {
            ConnectionBean mysqlConfig1 = makeConnBean();

            connectionMaker = new ConcreConnectionMaker(mysqlConfig1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return connectionMaker;
    }

    public ConnectionBean makeConnBean() {
        XMLParsingConfig parConfig = new XMLParsingConfig();
        ConnectionBean conConfig = null;
        try {
            conConfig = parConfig.setConfig();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return conConfig;
    }

}