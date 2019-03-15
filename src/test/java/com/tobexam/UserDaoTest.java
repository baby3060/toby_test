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
    private UserDao_Mod userDao;

    @Before
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        userDao = context.getBean("userDao", UserDao_Mod.class);
    }

    @Test
    public void addAndGet() {
        User user = new User();
        
        try {
            userDao.deleteAll();
            int count = userDao.countAll();
            
            assertThat(count, is(0));
            
            user = new User("1234", "1234", "12345");

            userDao.add(user);

            count = userDao.countAll();

            assertThat(count, is(1));

            User user2 = userDao.get("1234");

            assertThat(user2.getName(), is(user.getName()));
            assertThat(user2.getPassword(), is(user.getPassword()));
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 예외가 발생하면 테스트 성공
    @Test(expected=EmptyResultDataAccessException.class)
    public void getUserFailure() throws Exception, SQLException {
        
        // 예외 발생
        User user = userDao.get("unknown_id");
        // 예외가 안 발생할 때는? => 테스트 실패
        // User user = userDao.get("1234");
    }

    @Test
    public void updateAndGet() {
        
        try {
            int count = userDao.countAll();
            
            assertThat(count, is(1));
            
            User user = userDao.get("1234");

            assertThat(user.getName(), is("1234"));
            assertThat(user.getPassword(), is("12345"));
            
            user.setName("테스트");
            user.setPassword("테스트 비번");

            userDao.update(user);

            user = userDao.get("1234");

            assertThat(user.getName(), is("테스트"));
            assertThat(user.getPassword(), is("테스트 비번"));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
