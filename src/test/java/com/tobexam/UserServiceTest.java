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

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
    List<User> users;

    private UserService userService;
    private UserDao userDao;

    @Before
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        userService = context.getBean("userService", UserService.class);

        users = new ArrayList<User>(Arrays.asList(
            new User("1", "김길동", "비번1", Level.BASIC, 49, 0, "a@n.com"),
            new User("2", "배길동", "비번2", Level.SILVER, 60, 29, "b@n.com"),
            new User("3", "이길동", "비번3", Level.BASIC, 45, 0, "c@n.com"),
            new User("4", "고길동", "비번4", Level.GOLD, 60, 33, "d@n.com"),
            new User("5", "최길동", "비번5", Level.SILVER, 54, 30, "e@n.com"),
            new User("6", "박길동", "비번6", Level.BASIC, 51, 0, "f@n.com")
        ));

        userDao = context.getBean("userDao", UserDaoJdbc_Template.class);
    }

    @Test
    public void upgradeLevels() {
        try {
            userService.deleteAll();

            for(User user : users) {
                userService.add(user);
            }

            userService.upgradeLevels();

            checkLevelUpgraded(users.get(0), false);
            checkLevelUpgraded(users.get(1), false);
            checkLevelUpgraded(users.get(2), false);
            checkLevelUpgraded(users.get(3), false);
            checkLevelUpgraded(users.get(4), true);
            checkLevelUpgraded(users.get(5), true);
        } catch(Exception e) {

        }
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpgrade =null;
        try {
            userUpgrade = userDao.get(user.getId());
        } catch(Exception e) {

        }
        
        if( upgraded ) {
            assertThat(userUpgrade.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpgrade.getLevel(), is(user.getLevel()));
        }
        
    }

    @Test
    public void add() {
        try {
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

        } catch(Exception e) {

        }
    }

    


}