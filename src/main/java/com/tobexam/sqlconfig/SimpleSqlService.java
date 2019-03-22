package com.tobexam.sqlconfig;

import java.util.*;

public class SimpleSqlService implements SqlService {

    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public String getSql(String key) {
        return sqlMap.get(key);
    }
}