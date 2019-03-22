package com.tobexam.sqlconfig;

import java.util.*;

import com.tobexam.sqlconfig.jaxb.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * XML에서 SQL 읽어오기 위한 클래스
 */
public class XmlSqlService implements SqlService {
    private Map<String, String> sqlMap = new HashMap<String, String>();

    public XmlSqlService() {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            
            ClassLoader classLoader = getClass().getClassLoader();

            // XML을 자바 객체로 전환하기 위한 언마샬러
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(classLoader.getResourceAsStream("sqlmap.xml"));

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