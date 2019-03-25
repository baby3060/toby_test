package com.tobexam.context;

import com.tobexam.common.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import com.tobexam.sqlconfig.*;


import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

@Configuration
@Profile("test")
public class TestAppContext {

    @Bean
    public UserService testUserService() {
        return new UserServiceImpl.TestUserServiceImpl();
    }

    @Bean
    public MailSender mailSender() {
        DummyMailSender mailSender = new DummyMailSender();
        mailSender.setHost("mail.server.com");
        return mailSender;
    }

}