package com.tobexam.dao;

import java.util.*;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * jdbcContext 구현하는 인터페이스
 * @deprecated
 */
public interface Context {
    public void setDataSource(DataSource dataSource);
    public void executeSql(final String sql, Object ...param) throws SQLException;
    public int executeQueryOneInt(final String sql, Object ...param) throws SQLException;
    public <T extends Object> T executeQueryOneObject(final String sql, Class<T> type, Object ...param) throws SQLException;
    public <T extends Object> List<T> executeQueryList(final String sql, Class<T> type, Object ...param) throws SQLException;
}