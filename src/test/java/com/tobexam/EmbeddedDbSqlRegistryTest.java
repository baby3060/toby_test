package com.tobexam;

import java.util.*;

import org.junit.Test;

import com.tobexam.sqlconfig.*;

import org.junit.After;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
    
    EmbeddedDatabase db;
    
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
        .setType(HSQL)
        .addScript("classpath:/embsql/schema.sql")
        .build();

        EmbeddedDbSqlRegstry embeddedDbSqlRegistry = new EmbeddedDbSqlRegstry();
        embeddedDbSqlRegistry.setDataSource(db);

        return embeddedDbSqlRegistry;
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void transactionUpdate() {
        checkFindResult("SQL1", "SQL2", "SQL3");

        Map<String, String> sqlMap = new HashMap<String, String>();
        sqlMap.put("KEY1", "Modified1");
        sqlMap.put("KEYUNKOWN", "Modified999");

        try {   
            sqlRegistry.updateSql(sqlMap);
        } catch(SqlUpdateFailureException e) {}

        checkFindResult("SQL1", "SQL2", "SQL3");
    }


}