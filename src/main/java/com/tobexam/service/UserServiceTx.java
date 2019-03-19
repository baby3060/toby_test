package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

// 트랜잭션 경계설정 할 때 쓰이는 인터페이스(관리 인터페이스)
import org.springframework.transaction.PlatformTransactionManager;
// 구상 클래스
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

// 현 트랜잭션의 상태를 저장하기 위한 인터페이스 및 클래스
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTx implements UserService {
    
    @Autowired
    private UserService userService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        // DataSource를 이용하여 DB 작업을 하므로, DataSourceTransactionManager
        // 다른 프레임워크를 사용해도 바꿀 수도 있다.
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        
        try {
            this.userService.upgradeLevels();
            
            this.transactionManager.commit(status);
        } catch(RuntimeException e) {
            
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    public void deleteAll() {
        try {
            this.userService.deleteAll();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void add(User user) {
        this.userService.add(user);
    }

}