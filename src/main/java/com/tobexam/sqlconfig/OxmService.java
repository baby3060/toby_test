package com.tobexam.sqlconfig;

import com.tobexam.sqlconfig.jaxb.*;

import java.util.*;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import org.springframework.beans.factory.InitializingBean;

public class OxmService implements SqlService, InitializingBean {
    // init 부분과 getSql이 baseSqlService와 동일하다. 프록시에서 봤던 것처럼 이 클래스를 프록시로 두고, 위 두 메소드를 BaseSqlService에 위임한다.
    private final static BaseSqlService baseSqlService = new BaseSqlService();

    // Default 설정
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmap(Resource sqlmap) {
        this.oxmSqlReader.setSqlmap(sqlmap);
    }

    // OXM을 이용해서 SQL을 읽어오는 것이므로, Reader는 그대로 고정
    // Unmarshaller의 종류만 바꾸면 됨.
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

    private class OxmSqlReader implements SqlReader {
        private Unmarshaller unmarshaller;
        
        private Resource sqlmap = new ClassPathResource("sqlmap.xml");

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }
    
        public void setSqlmap(Resource sqlmap) {
            this.sqlmap = sqlmap;
        }

        public void readSql(SqlRegistry sqlRegistry) {
            try {
                Source xmlSource = new StreamSource(sqlmap.getInputStream());

                Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(xmlSource);

                List<SqlType> sqlList = sqlmap.getSql();

                for(SqlType type : sqlList) {
                    sqlRegistry.registerSql(type.getKey(), type.getValue());
                }
            } catch(IOException e) {
                throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.", e);
            }
        }
    }

    /**
     * SQL 초기화
     * 이 애노테이션이 붙으면 스프링은 해당 클래스 Bean을 생성하고, DI 작업을 마친 뒤 이 애노테이션이 달린 메소드를 실행한다.
     * PostConstruct 대신 InitializingBean 구현
     * XML 설정 읽기 => Bean 객체 생성 => DI => 후처리기 수행
     */
    public void afterPropertiesSet() throws Exception {
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);
        
        this.baseSqlService.afterPropertiesSet();
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.baseSqlService.getSql(key);
        } catch(SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }
}