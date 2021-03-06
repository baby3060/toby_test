package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.*;

import com.tobexam.model.*;
import com.tobexam.context.*;
import com.tobexam.dao.*;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppContext.class})
@ActiveProfiles("test")
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    User user1;
    User user2;
    User user3;

    @Before
    public void setUp() {
        user1 = new User("1", "이길동", "12345", Level.BASIC, 1, 0, "a@n.com");
        user2 = new User("2", "김길동", "12345", Level.SILVER, 55, 10, "b@n.com");
        user3 = new User("3", "최길동", "12345", Level.GOLD, 100, 40, "c@n.com");
    }

    @Test
    public void addAndGet() {
        
        try {
            userDao.deleteAll();
            int count = userDao.countAll();
            
            assertThat(count, is(0));
            
            userDao.add(user1);
            userDao.add(user2);
            userDao.add(user3);

            count = userDao.countAll();

            assertThat(count, is(3));

            User user = userDao.get("1");

            assertThat(user1.getName(), is(user.getName()));
            assertThat(user1.getPassword(), is(user.getPassword()));
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 예외가 발생하면 테스트 성공
    @Test(expected=EmptyResultDataAccessException.class)
    public void getUserFailure() {
        
        // 예외 발생
        User user = userDao.get("unknown_id");
        // 예외가 안 발생할 때는? => 테스트 실패
        // User user = userDao.get("1234");
    }

    @Test
    public void updateAndGet() {
        
        int count = userDao.countAll();
        assertThat(count, is(3));
        User user = userDao.get("1");
        count = userDao.count("1");
        assertThat(count, is(1));
        assertThat(user.getName(), is("이길동"));
        assertThat(user.getPassword(), is("12345"));
        
        user.setName("1234");
        user.setPassword("12345");
        userDao.update(user);
        user = userDao.get("1");
        assertThat(user.getName(), is("1234"));
        assertThat(user.getPassword(), is("12345"));   
    }

    @Test
    public void selectAll() {
        userDao.deleteAll();
        List<User> users0 = userDao.selectAll();
        assertThat(users0.size(), is(0));
        userDao.add(user1);
        List<User> users1 = userDao.selectAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));
        userDao.add(user2);
        List<User> users2 = userDao.selectAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));
        userDao.add(user3);
        List<User> users3 = userDao.selectAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    @Test
    public void update() {
        
        userDao.deleteAll();
        userDao.add(user1); 
        user1.setRecommend(1000);
        userDao.update(user1);
        User user1Update = userDao.get(user1.getId());
        checkSameUser(user1, user1Update);
       
    }

    private void checkSameUser(User target, User source) {
        assertThat(target.getId(), is(source.getId()));
        assertThat(target.getName(), is(source.getName()));
        assertThat(target.getPassword(), is(source.getPassword()));
        assertThat(target.getLevel(), is(source.getLevel()));
        assertThat(target.getLogin(), is(source.getLogin()));
        assertThat(target.getRecommend(), is(source.getRecommend()));
        assertThat(target.getEmail(), is(source.getEmail()));
    }
}
