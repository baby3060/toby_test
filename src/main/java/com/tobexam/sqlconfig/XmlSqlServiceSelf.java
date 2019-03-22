package com.tobexam.sqlconfig;

import java.util.*;

import com.tobexam.sqlconfig.jaxb.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.annotation.PostConstruct;

/**
 * @deprecated
 */
public class XmlSqlServiceSelf implements SqlService, SqlReader, SqlRegistry {

    Map<String, String> sqlMap = new HashMap<String, String>();

    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;

    public void setSqlmapFile(String sqlmapFile)  {
        this.sqlmapFile = sqlmapFile;
    }

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    // SqlService

    @PostConstruct
    public void initSql() {
        this.sqlReader.readSql(this.sqlRegistry);
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        
        try {
            return this.sqlRegistry.findSql(key);
        } catch(SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }

    // SqlReader

    private String sqlmapFile;

    public void readSql(SqlRegistry sqlRegistry) {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            
            ClassLoader classLoader = getClass().getClassLoader();

            // XML을 자바 객체로 전환하기 위한 언마샬러
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(classLoader.getResourceAsStream(this.sqlmapFile));

            for( SqlType sql : sqlmap.getSql() ) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }

        } catch(JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // SqlRegistry

    public void registerSql(String key, String value) {
        sqlMap.put(key, value);
    }

    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if( sql == null || sql.equals("") ) {
            throw new SqlNotFoundException(key + "를 이용해서 SQL 문을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }
}