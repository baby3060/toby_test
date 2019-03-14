package com.tobexam;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class UserDaoTest {
    @Test
    public void addAndGet() {
        User user = new User();
        
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao userDao = context.getBean("userDao", UserDao.class);
        
        try {
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
