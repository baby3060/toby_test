package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import javax.sql.DataSource;

import java.util.*;

// 일반적인 java Mail API 사용
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {
    
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    private UserDao userDao;
    
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.selectAll();
        boolean changed = false;
        for( User user : users ) {
            if( canUpgradeLevel(user) ) {
                upgradeLevel(user);
            }
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

    // Test를 위하여 protected로 지정
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
        if( !user.getEmail().equals("")) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setFrom("owner@ow.com");
            mailMessage.setSubject("Upgrade 안내");
            mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

            this.mailSender.send(mailMessage);
        } else {
            System.out.println("로깅 남김 - 사용자의 메일(" + user.getId() + ")이 없습니다.");
        }
    }

    public void update(User user) {
        this.userDao.update(user);
    }
    public void delete(User user) {
        this.userDao.delete(user);
    }
    
    @Transactional(readOnly=true)
    public User get(String id) {
        return this.userDao.get(id);
    }
    
    @Transactional(readOnly=true)
    public int count(String id) {
        return this.userDao.count(id);
    }
    
    @Transactional(readOnly=true)
    public int countAll() {
        return this.userDao.countAll();
    }

    @Transactional(readOnly=true)
    public List<User> selectAll() {
        return this.userDao.selectAll();
    }

}