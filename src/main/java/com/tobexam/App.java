package com.tobexam;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import com.tobexam.factory.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class App {
    public static void main( String[] args ) {

        User user = new User();
        
        // ApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);

        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao userDao = context.getBean("userDao", UserDao.class);
        
        // CountingConnectionMaker countMaker = context.getBean("connectionMaker", CountingConnectionMaker.class);

        try {
            user = userDao.get("111");

            System.out.println(user);

            // System.out.println("Connection counter(1) : " + countMaker.getCounter());

            int count = userDao.countAll();

            System.out.println("All Count : " + count);

            // System.out.println("Connection counter(2) : " + countMaker.getCounter());

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
