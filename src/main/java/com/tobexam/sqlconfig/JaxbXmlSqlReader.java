package com.tobexam.sqlconfig;

import com.tobexam.sqlconfig.jaxb.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class JaxbXmlSqlReader implements SqlReader {
    // 기본 SQL map 파일(Default 대비)
    private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";

    private String sqlmapFile = DEFAULT_SQLMAP_FILE;
    
    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

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
}