package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import javax.sql.DataSource;

import java.util.*;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;

// 트랜잭션 경계설정 할 때 쓰이는 인터페이스(관리 인터페이스)
import org.springframework.transaction.PlatformTransactionManager;
// 구상 클래스
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

// 현 트랜잭션의 상태를 저장하기 위한 인터페이스 및 클래스
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    
    private static final String SMTP_HOST = "mail.host.com";
    private static final String SEND_FROM = "fromemail@gmail.com";
    private static final String USER_NAME = "webmaster";
    private static final String USER_PASS = "webpass";


    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PlatformTransactionManager transactionManager;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        // DataSource를 이용하여 DB 작업을 하므로, DataSourceTransactionManager
        // 다른 프레임워크를 사용해도 바꿀 수도 있다.
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() throws Exception {
        
        // 트랜잭션 관리자에서 해당 트랜잭션을 가져오라.
        // DefaultTransactionDefinition는 트랜잭션에 대한 속성을 지니고 있다.
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.selectAll();
            boolean changed = false;
            for( User user : users ) {
                if( canUpgradeLevel(user) ) {
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch(Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch(currentLevel) {
            case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER : return (user.getRecommend() >= MIN_RECOMEND_FOR_GOLD);
            case GOLD : return false;
            default : throw new IllegalArgumentException("Unknown Level : " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    public void deleteAll() {
        try {
            userDao.deleteAll();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void add(User user) {
        try {
            if( user.getLevel() == null ) {
                user.setLevel(Level.BASIC);
            }
            userDao.add(user);
        } catch(Exception e) {
            System.out.println("로깅 실시");
            e.printStackTrace();
        }
    }

    private void sendUpgradeEmail(User user) {
        if( !user.getEmail().equals("") ) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "25");
            
            Session mailSession = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER_NAME, USER_PASS);
                }
            });

            try {
                MimeMessage message = new MimeMessage(mailSession);
                
                message.setFrom(new InternetAddress(SEND_FROM));
                
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
                
                message.setSubject("Upgrade 안내");
                
                message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");

                // Send message
                Transport.send(message);

            } catch(AddressException addrE) {

            } catch(MessagingException msgE) {

            } 

        } else {
            System.out.println("로깅 남김 - 사용자의 메일(" + user.getId() + ")이 없습니다.");
        }
    }

}