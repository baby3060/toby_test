package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import org.junit.runner.RunWith;

import javax.sql.DataSource;


import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.mail.MailSender;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
    static class TestUserService extends UserServiceImpl {
        private String id;
    
        private TestUserService(String id) {
            this.id = id;
        }
        
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            } else {
                super.upgradeLevel(user);
            }
        }
    }
    
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();
    
        public List<String> getRequests() {
            return requests;
        }
    
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }
        public void send(SimpleMailMessage[] mailMessageArr) throws MailException {}
    }

    @Autowired
    private MailSender mailSender;

    static class TestUserServiceException extends RuntimeException { }

    List<User> users;

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserDao userDao;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PlatformTransactionManager transactionManager;


    @Before
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        users = new ArrayList<User>(Arrays.asList(
            new User("1", "김길동", "비번1", Level.BASIC, 49, 0, "a@n.com"),
            new User("2", "배길동", "비번2", Level.SILVER, 60, 31, "b@n.com"),
            new User("3", "이길동", "비번3", Level.BASIC, 45, 0, "c@n.com"),
            new User("4", "고길동", "비번4", Level.GOLD, 60, 33, "d@n.com"),
            new User("5", "최길동", "비번5", Level.SILVER, 54, 30, "e@n.com"),
            new User("6", "박길동", "비번6", Level.BASIC, 51, 0, "f@n.com")
        ));
    }

    @Test
    public void upgradeLevels() {
        userServiceImpl.deleteAll();
        for(User user : users) {
            userServiceImpl.add(user);
        }
        
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        try {
            userServiceImpl.upgradeLevels();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), false);
        checkLevelUpgraded(users.get(4), true);
        checkLevelUpgraded(users.get(5), true);

        List<String> request = mockMailSender.getRequests();

        assertThat(request.size(), is(3));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(4).getEmail()));
        assertThat(request.get(2), is(users.get(5).getEmail()));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpgrade = userDao.get(user.getId());
        
        if( upgraded ) {
            assertThat(userUpgrade.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpgrade.getLevel(), is(user.getLevel()));
        }
        
    }

    @Test
    public void add() {
        userService.deleteAll();

        User userWithLevel = users.get(0);
        User userWithoutLevel = users.get(4);
        userWithoutLevel.setLevel(null);
        
        userService.add(userWithLevel);
        userService.add(userWithoutLevel);
        
        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithOutLevelRead = userDao.get(userWithoutLevel.getId());
        
        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
    }

    
}