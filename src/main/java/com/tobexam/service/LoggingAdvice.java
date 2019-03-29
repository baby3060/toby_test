package com.tobexam.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws java.lang.Throwable {
        Class clsx = invocation.getThis().getClass();

        Logger logger = LoggerFactory.getLogger(clsx);

        Object ret = invocation.proceed();

        logger.info(invocation.getMethod().getName() + " Called");

        return ret;
    }
}