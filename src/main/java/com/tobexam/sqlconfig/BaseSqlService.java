package com.tobexam.sqlconfig;

import java.util.*;

import com.tobexam.sqlconfig.jaxb.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.annotation.PostConstruct;

public class BaseSqlService implements SqlService {
    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;
    
    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    /**
     * SQL 초기화
     * 이 애노테이션이 붙으면 스프링은 해당 클래스 Bean을 생성하고, DI 작업을 마친 뒤 이 애노테이션이 달린 메소드를 실행한다.
     * XML 설정 읽기 => Bean 객체 생성 => DI => 후처리기 수행
     */
    @PostConstruct
    public void initSql() {
        this.sqlReader.readSql(this.sqlRegistry);
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch(SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }
}