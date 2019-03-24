package com.tobexam;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class EmbededDBTest {
    EmbeddedDatabase db;
    NamedParameterJdbcTemplate template;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
        .setType(HSQL)
        .addScript("classpath:/embsql/schema.sql")
        .addScript("classpath:/embsql/data.sql")
        .build();

        template = new NamedParameterJdbcTemplate(db);
    }


    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void initData() {
        assertThat(template.queryForObject("Select Count(*) From sqlmap", new HashMap<String, String>(), Integer.class), is(2));

        List<Map<String, Object>> list = template.queryForList("Select * From sqlmap Order By key_", new HashMap<String, String>());

        assertThat((String)list.get(0).get("key_"), is("KEY1"));
        assertThat((String)list.get(0).get("sql_"), is("SQL1"));
        assertThat((String)list.get(1).get("key_"), is("KEY2"));
        assertThat((String)list.get(1).get("sql_"), is("SQL2"));
    }

    @Test
    public void insert() {
        Map<String, String> param = new HashMap<String, String>();

        param.put("key_", "KEY3");
        param.put("sql_", "SQL3");

        template.update("insert into sqlmap(key_, sql_) values (:key_, :sql_)", param);

        assertThat(template.queryForObject("Select Count(*) From sqlmap", new HashMap<String, String>(), Integer.class), is(3));
    }

}