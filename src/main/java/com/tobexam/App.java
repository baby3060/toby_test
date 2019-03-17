package com.tobexam;

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
        
        try {
            userDao.deleteAll();

            user = new User("1234", "1234", "12345");

            userDao.add(user);

            user = new User("didii1l", "김길동", "12345");

            userDao.add(user);

            User oriUser = userDao.get("1234");

            System.out.println(oriUser);
            
            oriUser.setName("테스트");
            oriUser.setPassword("테스트 비번");

            userDao.update(oriUser);

            oriUser = userDao.get("1234");
            
            System.out.println(oriUser);

            int count = userDao.countAll();

            System.out.println(count);

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
