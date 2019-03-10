package com.tobexam;

import com.tobexam.common.*;
import com.tobexam.model.*;
import com.tobexam.dao.*;

public class App {
    public static void main( String[] args ) {

        XMLParsingConfig parConfig = new XMLParsingConfig();

        User user = new User();
        
        try {
        	ConnectionBean mysqlConfig1 = parConfig.setConfig("mysql_conn.xml");

            SimpleConnectionMaker connectionMaker = new SimpleConnectionMaker(mysqlConfig1);

            UserDao userDao = new UserDao(connectionMaker);

            user = userDao.get("111");

            System.out.println(user);

            int count = userDao.countAll();

            System.out.println("All Count : " + count);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
