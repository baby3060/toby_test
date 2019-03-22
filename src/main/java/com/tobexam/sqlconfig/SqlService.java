package com.tobexam.sqlconfig;

public interface SqlService {
    public String getSql(String key) throws SqlretrievalFailureException;
}