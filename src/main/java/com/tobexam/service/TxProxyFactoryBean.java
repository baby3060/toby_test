package com.tobexam.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * 프록시를 Bean으로 등록하기 위해 FactoryBean을 구현(1)
 * 이것보다 더 나은 버전은 ProxyFactoryBean을 사용
 * @deprecated
 */
public class TxProxyFactoryBean implements FactoryBean {

    // TransactionHandler를 생성할 때 필요
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;


    // 다이내믹 프록시 생성 시 필요(타깃 인터페이스)
    private Class<?> serviceInterface;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    // FactoryBean 인터페이스에서 제공하는 Bean 객체를 생성해주는 메소드
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        // 적용 객체
        txHandler.setTarget(target);
        // 트랜잭션 매니저
        txHandler.setTransactionManager(transactionManager);
        // 어떤 팩토리에 적응할 것인지?
        txHandler.setPattern(pattern);

        return Proxy.newProxyInstance(
            getClass().getClassLoader()
            , new Class[] { this.serviceInterface }
            , txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}