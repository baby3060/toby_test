package com.tobexam.sqlconfig;

import com.tobexam.sqlconfig.jaxb.*;

import java.util.*;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.annotation.PostConstruct;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

public class OxmService implements SqlService {
    // Default 설정
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmapFile(String sqlmapFile) {
        this.oxmSqlReader.setSqlmapFile(sqlmapFile);
    }

    // OXM을 이용해서 SQL을 읽어오는 것이므로, Reader는 그대로 고정
    // Unmarshaller의 종류만 바꾸면 됨.
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

    private class OxmSqlReader implements SqlReader {
        private Unmarshaller unmarshaller;
        
        private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
        private String sqlmapFile = DEFAULT_SQLMAP_FILE;

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }
    
        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }

        public void readSql(SqlRegistry sqlRegistry) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();

                Source xmlSource = new StreamSource(classLoader.getResourceAsStream("sqlmap.xml"));

                Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(xmlSource);

                List<SqlType> sqlList = sqlmap.getSql();

                for(SqlType type : sqlList) {
                    sqlRegistry.registerSql(type.getKey(), type.getValue());
                }
            } catch(IOException e) {
                throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다.", e);
            }
        }
    }

    @PostConstruct
    public void initXml() {
        this.oxmSqlReader.readSql(this.sqlRegistry);
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch(SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }
}