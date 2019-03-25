package com.tobexam;

import java.util.*;

import com.tobexam.context.*;

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

import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppContext.class})
@ActiveProfiles("test")
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
        assertThat(template.queryForObject("Select Count(*) From sqlmap", new HashMap<String, String>(), Integer.class), is(0));
    }

    @Test
    public void insert() {
        Map<String, String> param = new HashMap<String, String>();

        param.put("key_", "KEY3");
        param.put("sql_", "SQL3");

        template.update("insert into sqlmap(key_, sql_) values (:key_, :sql_)", param);

        assertThat(template.queryForObject("Select Count(*) From sqlmap", new HashMap<String, String>(), Integer.class), is(1));
    }

}