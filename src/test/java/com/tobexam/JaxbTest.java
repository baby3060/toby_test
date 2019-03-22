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

        // 테스트클래스와 같은 위치 일 경우
        // Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));

        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size(), is(8));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("Insert Into USER(id, name, password, level, login, recommend, email) Values (?, ?, ?, ?, ?, ?, ?)"));
        assertThat(sqlList.get(1).getKey(), is("deleteAll"));
        assertThat(sqlList.get(1).getValue(), is("Delete From USER"));
        assertThat(sqlList.get(2).getKey(), is("update"));
        assertThat(sqlList.get(2).getValue(), is("Update USER set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? Where id = ?"));
        assertThat(sqlList.get(3).getKey(), is("delete"));
        assertThat(sqlList.get(3).getValue(), is("Delete From USER Where id = ?"));
        assertThat(sqlList.get(4).getKey(), is("get"));
        assertThat(sqlList.get(4).getValue(), is("Select * From USER Where id = ?"));
        assertThat(sqlList.get(5).getKey(), is("count"));
        assertThat(sqlList.get(5).getValue(), is("Select Count(*) As cnt From USER Where id = ?"));
        assertThat(sqlList.get(6).getKey(), is("countAll"));
        assertThat(sqlList.get(6).getValue(), is("Select Count(*) As cnt From USER"));
        assertThat(sqlList.get(7).getKey(), is("selectAll"));
        assertThat(sqlList.get(7).getValue(), is("Select * From USER Order By id"));
    }
}