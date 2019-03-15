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
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
