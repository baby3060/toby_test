package com.tobexam;

import com.tobexam.sqlconfig.jaxb.*;

import java.util.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

// 언마샬링 : XML => 자바 객체
// 마샬링 : 자바 객체 => XML
public class JaxbTest {
    
    @Test
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);

        // Maven의 Resource 경로 감안하여 클래스로더를 들고옴
        ClassLoader classLoader = getClass().getClassLoader();

        Unmarshaller unmarshaller = context.createUnmarshaller();

        Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(classLoader.getResourceAsStream("sqlmap.xml"));

        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size(), is(3));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("insert"));
        assertThat(sqlList.get(1).getKey(), is("get"));
        assertThat(sqlList.get(1).getValue(), is("select"));
        assertThat(sqlList.get(2).getKey(), is("delete"));
        assertThat(sqlList.get(2).getValue(), is("delete"));
    }
}