package com.tobexam;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class App {
    public static void main( String[] args ) {
        
        User user = new User();
        
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao userDao = context.getBean("userDao", UserDao.class);
        
        try {
            user = userDao.get("111");

            System.out.println(user);

            int count = userDao.countAll();

            System.out.println("All Count : " + count);

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
