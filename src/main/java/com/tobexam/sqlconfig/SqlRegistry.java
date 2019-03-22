package com.tobexam.sqlconfig;

public interface SqlRegistry {
    public void registerSql(String key, String value);

    public String findSql(String key) throws SqlNotFoundException;
}