package com.tobexam.common;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Bean 후처리기 구현
 */
public class LoggerPostProcessor implements BeanPostProcessor {
 
    public Object postProcessAfterInitialization(Object target, String beanName) throws BeansException {
        return target;
    }
    
    // ReflectionUtils.doWithFields : 해당 타겟 클래스의 모든 필드에 대해 콜백 메소드 실행
    public Object postProcessBeforeInitialization(final Object target, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(target.getClass(), new FieldCallback() {
                @SuppressWarnings("unchecked")
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    // 주어진 필드에 접근 가능하게 만듦
                    ReflectionUtils.makeAccessible(field);
 
                    // Field의 애노테이션이 Log라면?
                    if (field.getAnnotation(Log.class) != null) {
                        // Log 애노테이션
                        Log logAnnotation = field.getAnnotation(Log.class);
                        // Logger 생성(대상 클래스의)
                        Logger logger = LoggerFactory.getLogger(target.getClass());
                        // 대상 필드롤 Logger로 설정
                        field.set(target, logger);
                    }
                }
        });
 
        return target;
    }
}