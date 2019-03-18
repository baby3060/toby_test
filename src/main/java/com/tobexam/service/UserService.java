package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import javax.sql.DataSource;
import java.sql.Connection;

import java.util.*;

// 트랜잭션 동기화 관리자 => 동기화 작업 초기화하기 위한
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class UserService {
    private UserDao userDao;
    private DataSource dataSource;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.selectAll();
            boolean changed = false;
            for( User user : users ) {
                if( canUpgradeLevel(user) ) {
                    upgradeLevel(user);
                }
            }
            c.commit();
        } catch(Exception e) {
            c.rollback();
            throw e;
        } finally {
            // DB 커넥션 해제
            DataSourceUtils.releaseConnection(c, this.dataSource);
            // 동기화 작업 종료
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
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
        try {
            userDao.update(user);
        } catch(Exception e) {
            System.out.println("로깅 실시");
            e.printStackTrace();
        }
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
}