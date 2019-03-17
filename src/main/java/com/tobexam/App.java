package com.tobexam;

import com.tobexam.service.*;
import com.tobexam.model.*;
import com.tobexam.dao.*;

import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class App {
    public static void main( String[] args ) {
        
        User user = new User();
        
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao userDao = context.getBean("userDao", UserDaoJdbc_Template.class);
        UserService userService = context.getBean("userService", UserService.class);
        try {
            userDao.deleteAll();

            user = new User("1234", "1234", "12345", Level.BASIC, 0, 0);

            userDao.add(user);

            user = new User("didii1l", "김길동", "12345", Level.BASIC, 0, 0);

            userDao.add(user);

            userService.upgradeLevels();

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
