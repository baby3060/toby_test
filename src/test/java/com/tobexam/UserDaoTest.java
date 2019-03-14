package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import org.junit.runner.RunWith;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {
    private UserDao userDao;

    @Before
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        userDao = context.getBean("userDao", UserDao.class);
    }

    @Test
    public void addAndGet() {
        User user = new User();
        
        try {
            int result = userDao.deleteAll();
            int count =userDao.countAll();
            
            assertThat(userDao.countAll(), is(0));
            
            user = new User("1234", "1234", "12345");

            result = userDao.add(user);

            assertThat(userDao.countAll(), is(1));

            User user2 = userDao.get("1234");

            assertThat(user2.getName(), is(user.getName()));
            assertThat(user2.getPassword(), is(user.getPassword()));
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected=EmptyResultDataAccessException.class)
    public void getUserFailure() throws Exception, SQLException {
        
        // 예외 발생
        User user = userDao.get("unknown_id");
        // 예외가 안 발생할 때는? => 테스트 실패
        // User user = userDao.get("1234");
    }

}
