package com.tobexam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * jdbcContext에서 사용하는 PreparedStatement 생성용 전략 인터페이스
 * @deprecated
 */
public interface StatementStrategy {
    public PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}