package com.tobexam.sqlconfig;

import java.util.*;

public class HashMapSqlRegistry implements SqlRegistry {
    Map<String, String> sqlMap = new HashMap<String, String>();

    public void registerSql(String key, String value) {
        sqlMap.put(key, value);
    }

    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if( sql == null || sql.equals("") ) {
            throw new SqlNotFoundException(key + "를 이용해서 SQL 문을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }
}