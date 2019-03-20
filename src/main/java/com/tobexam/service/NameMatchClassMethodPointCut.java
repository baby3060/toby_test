package com.tobexam.service;

import org.springframework.util.PatternMatchUtils;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;

/**
 * NameMatchMethodPointcut는 기본적으로 포인트컷을 Method 기준으로 작성한다.
 */
public class NameMatchClassMethodPointCut extends NameMatchMethodPointcut {
    // xml의 pointcut Bean 작성 property
    public void setMappedClassName(String mappedClassName) {
        this.setClassFilter(new SimpleClassFilter(mappedClassName));
    }

    static class SimpleClassFilter implements ClassFilter {
        String mappedName;

        private SimpleClassFilter(String mappedName) {
            this.mappedName = mappedName;
        }

        public boolean matches(Class<?> clazz) {
            // simpleMatch : 와일드카드가 들어간 문자열 비교를 지원하는 유틸리티 메소드
            return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
        }
    }
}