package com.tobexam;

import com.tobexam.sqlconfig.jaxb.*;

import java.util.*;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/OxmTest-applicationContext.xml")
public class OxmTest {
    @Autowired
    Unmarshaller unmarshaller;

    @Test
    public void unmarshallSqlMap() throws XmlMappingException, IOException {
        // Maven Resource 특성 상 루트로 이동되므로, 클래스로더로부터 Resource를 가지고옴
        ClassLoader classLoader = getClass().getClassLoader();

        Source xmlSource = new StreamSource(classLoader.getResourceAsStream("sqlmap.xml"));

        Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(xmlSource);

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