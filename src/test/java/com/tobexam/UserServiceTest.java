package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Proxy;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

import java.util.*;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import com.tobexam.service.*;
import org.junit.runner.RunWith;

import javax.sql.DataSource;

import org.springframework.aop.framework.ProxyFactoryBean;

import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.test.annotation.DirtiesContext;
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
    @Autowired
    ApplicationContext context;

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

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<User>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        public List<User> selectAll() {
            return this.users;
        }

        public void update(User user) {
            updated.add(user);
        }

        public void setDataSource(DataSource dataSource) { throw new UnsupportedOperationException(); } 
        public void add(User user) { throw new UnsupportedOperationException(); }
        public void deleteAll() { throw new UnsupportedOperationException(); }
        public User get(String id) { throw new UnsupportedOperationException(); }
        public void delete(User user) { throw new UnsupportedOperationException(); }
        public int count(String id) { throw new UnsupportedOperationException(); }
        public int countAll() { throw new UnsupportedOperationException(); }
    }

    @Autowired
    private MailSender mailSender;

    static class TestUserServiceException extends RuntimeException { }

    List<User> users;

    @Autowired
    private UserService userService;

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
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(4).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService)txProxyFactoryBean.getObject();

        userDao.deleteAll();

        for(User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch(TestUserServiceException e) {
            
        }

        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    public void upgradeAllOrNothingDynamicProxy() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(4).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");

        // 다이내믹 프록시 생성
        UserService txUserService = (UserService)Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { UserService.class },
            txHandler
        );
        userDao.deleteAll();

        for(User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch(TestUserServiceException e) {
            
        }

        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    // 이 테스트에서는 데이터를 DB에 넣을 필요없이, List만을 가지고 테스트할 수 있다.
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(3));
        checkUserAndLevel(updated.get(0), "2", Level.GOLD);
        checkUserAndLevel(updated.get(1), "5", Level.GOLD);
        checkUserAndLevel(updated.get(2), "6", Level.SILVER);
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
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

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        // Return값 설정
        when(mockUserDao.selectAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        // hamcrest의 is와 겹치므로, 아래와 같이 지정
        verify(mockUserDao, times(3)).update(org.mockito.Mockito.any(User.class));
        verify(mockUserDao, times(3)).update(org.mockito.Mockito.any(User.class));
        verify(mockUserDao, times(3)).update(org.mockito.Mockito.any(User.class));

        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(), is(Level.GOLD));

        verify(mockUserDao).update(users.get(4));
        assertThat(users.get(4).getLevel(), is(Level.GOLD));

        verify(mockUserDao).update(users.get(5));
        assertThat(users.get(5).getLevel(), is(Level.SILVER));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(3)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(4).getEmail()));
        assertThat(mailMessages.get(2).getTo()[0], is(users.get(5).getEmail()));
    }

    
}