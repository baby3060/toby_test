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

        UserDao_Mod userDao = context.getBean("userDao", UserDao_Mod.class);
        
        try {
            userDao.deleteAll();

            user = new User("1234", "1234", "12345");

            userDao.add(user);

            System.out.println(user);

            user = userDao.get("1234");

            user.setName("테스트");
            user.setPassword("테스트 비번");

            userDao.update(user);

            user = userDao.get("1234");

            System.out.println(user);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
