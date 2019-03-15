package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.springframework.dao.EmptyResultDataAccessException;

import com.tobexam.common.*;
import com.tobexam.model.*;
import com.tobexam.dao.*;

/**
 * 스프링 없이 실행한 테스트
 */
public class UserDaoTest_NoSpring {
    private ConnectionBean connBean;
    private XMLParsingConfig config;

    private UserDao_Mod userDao;

    @Before
    public void setUp() {
        config = new XMLParsingConfig();
        config.setFileName("mysql_conn.xml");
        try {
            connBean = config.setConfig();

            userDao = new UserDao_Mod();

            DataSource dataSource = new SingleConnectionDataSource(connBean.getConnStr(), connBean.getUserName(), connBean.getUserPass(), true);
            userDao.setDataSource(dataSource);
        } catch(Exception e) {

        }
        
    }

    @Test
    public void addAndGet2() {
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

    @Test(expected=EmptyResultDataAccessException.class)
    public void getUserFailure2() throws Exception, SQLException {
        
        // 예외 발생
        User user = userDao.get("unknown_id");
        // 예외가 안 발생할 때는? => 테스트 실패
        // User user = userDao.get("1234");
    }

}
