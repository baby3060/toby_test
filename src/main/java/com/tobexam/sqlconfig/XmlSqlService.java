package com.tobexam.sqlconfig;

import java.util.*;

import com.tobexam.sqlconfig.jaxb.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.InitializingBean;
/**
 * XML에서 SQL 읽어오기 위한 클래스
 */
public class XmlSqlService implements SqlService, InitializingBean {
    private Map<String, String> sqlMap = new HashMap<String, String>();

    private String sqlmapFile;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    /**
     * SQL 초기화
     * 이 애노테이션이 붙으면 스프링은 해당 클래스 Bean을 생성하고, DI 작업을 마친 뒤 이 애노테이션이 달린 메소드를 실행한다.
     * PostConstruct 대신 InitializingBean 구현
     * XML 설정 읽기 => Bean 객체 생성 => DI => 후처리기 수행
     */
    public void afterPropertiesSet() throws Exception {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            
            ClassLoader classLoader = getClass().getClassLoader();

            // XML을 자바 객체로 전환하기 위한 언마샬러
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(classLoader.getResourceAsStream(this.sqlmapFile));

            for( SqlType sql : sqlmap.getSql() ) {
                sqlMap.put(sql.getKey(), sql.getValue());
            }

        } catch(JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);

        if( sql == null || sql.equals("") ) {
            throw new SqlRetrievalFailureException(key + "를 이용해서 SQL 문을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }
}