package com.tobexam.sqlconfig;

import java.util.*;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedDbSqlRegstry implements UpdatableSqlRegistry {
    JdbcTemplate template;

    public void setDataSource(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    public void registerSql(String key, String value) {
        this.template.update("Insert Into sqlmap(key_, sql_) values (?, ?)", new Object[]{key, value});
    }

    public String findSql(String key) throws SqlNotFoundException {
        try {
            return template.queryForObject("Select sql_ From SQLMAP Where key_ = ?", new Object[]{key}, String.class);
        } catch(IncorrectResultSizeDataAccessException e) {
            throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    public void updateSql(String key, String sql) throws SqlUpdateFailureException {

        int affected = this.template.update("Update sqlmap set sql_ = ? Where key_ = ?", new Object[]{sql, key});

        if( affected == 0 ) {
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
        for( Map.Entry<String, String> entry : sqlMap.entrySet() ) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }
}