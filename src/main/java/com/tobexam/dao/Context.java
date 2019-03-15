package com.tobexam.dao;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface Context {
    public void setDataSource(DataSource dataSource);
    public void updateStrategyContext(StatementStrategy strategy, Result result) throws SQLException;
}