package com.tobexam.sqlconfig;

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
    private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();

    public void registerSql(String key, String value) {
        sqlMap.put(key, value);
    }

    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);

        if( sql == null ) {
            throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }

    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        if( sqlMap.get(key) == null ) {
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }

        sqlMap.put(key, sql);
    }

    public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
        for( Map.Entry<String, String> entry : sqlMap.entrySet() ) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }
}