package com.tobexam.service;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 부가기능 부여해주는 클래스(어드바이스)
 */
public class TransactionAdvice implements MethodInterceptor {
    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object invoke(MethodInvocation invocation)  throws Throwable {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();

        Method method = invocation.getMethod();

        String methodName = method.getName();

        if( methodName.startsWith("upgrades") ) {
            
        } else {
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        }

        TransactionStatus status = this.transactionManager.getTransaction(definition);

        try {
            Object ret = invocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch(RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}