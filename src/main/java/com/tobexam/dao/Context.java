package com.tobexam.dao;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface Context {
    public void setDataSource(DataSource dataSource);
    public void executeSql(final String sql, Object ...param) throws SQLException;
    public int executeQueryOneInt(final String sql, Object ...param) throws SQLException;
}