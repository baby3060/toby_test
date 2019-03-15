package com.tobexam.dao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class JdbcContext implements Context {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 전략을 입력받아서 executeUpdate를 행하는 메소드
    // try ~ catch ~ finally 부분을 분리하였다.
    public void updateStrategyContext(StatementStrategy strategy, Result result) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();

            pstmt = strategy.makePreparedStatement(conn);

            result.setResult(pstmt.executeUpdate());
        } catch(SQLException e) {
            throw e;
        } finally {
            if(pstmt != null) { try { pstmt.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(conn != null) { try { conn.close(); } catch(Exception e) { e.printStackTrace(); } }
        }
    }

    public void executeSql(final String sql, Object ...param) throws SQLException {

        Result result = new Result();

        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                int index = 1;

                PreparedStatement pstmt = connection.prepareStatement(sql);

                if( param.length > 0 ) {
                    for(Object obj : param) {
                        if( obj instanceof Integer ) {
                            pstmt.setInt(index, (Integer)obj);
                        } else if( obj instanceof String ) {
                            pstmt.setString(index, (String)obj);
                        } else if( obj instanceof Double ) {
                            pstmt.setDouble(index, (Double)obj);
                        } else if( obj instanceof Float ) {
                            pstmt.setFloat(index, (Float)obj);
                        } else if( obj instanceof Long ) {
                            pstmt.setLong(index, (Long)obj);
                        }
                        index++;
                    }
                }

                return pstmt;
            }
        }, result);

        
    }
}