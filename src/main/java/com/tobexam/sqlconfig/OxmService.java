package com.tobexam.sqlconfig;

import com.tobexam.sqlconfig.jaxb.*;

import java.util.*;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;


public class OxmService implements SqlService {
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

    @PostConstruct
    public void initXml() {
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);
        
        this.baseSqlService.initSql();
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.baseSqlService.getSql(key);
        } catch(SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }
}