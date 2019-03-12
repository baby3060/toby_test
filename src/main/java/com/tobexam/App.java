package com.tobexam;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import com.tobexam.factory.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App {
    public static void main( String[] args ) {

        User user = new User();
        
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

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
