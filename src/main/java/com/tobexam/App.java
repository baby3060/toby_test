package com.tobexam;

import com.tobexam.service.*;
import com.tobexam.model.*;
import com.tobexam.dao.*;

import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@Component
public class App {
    @Autowired
    private UserService userService;

    public static void main( String[] args ) {
        System.out.println("Test 시작");

        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        App p = context.getBean(App.class);

        p.start();
    }

    private void start() {

        User newUser = new User("2", "이길동", "12345", Level.BASIC, 0, 0, "a@n.com");

        try {
            this.userService.updateRecommend(newUser, "1");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}