package com.tobexam.dao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class JdbcContext implements Context {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 전략을 입력받아서 executeUpdate를 행하는 메소드
    // try ~ catch ~ finally 부분을 분리하였다.
    public void updateStrategyContext(StatementStrategy strategy) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();

            pstmt = strategy.makePreparedStatement(conn);

            pstmt.executeUpdate();
        } catch(SQLException e) {
            throw e;
        } finally {
            if(pstmt != null) { try { pstmt.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(conn != null) { try { conn.close(); } catch(Exception e) { e.printStackTrace(); } }
        }
    }

    // executeUpdate문 대비 실행문
    public void executeSql(final String sql, Object ...param) throws SQLException {
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
        });
    }

    public int queryStrategyContext(StatementStrategy stateStrategy) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int result = 0;
        
        try {
            conn = dataSource.getConnection();

            pstmt = stateStrategy.makePreparedStatement(conn);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                result = rs.getInt(1);
            }
        } catch(SQLException e) {
            throw e;
        } finally {
            if(rs != null) { try { rs.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(pstmt != null) { try { pstmt.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(conn != null) { try { conn.close(); } catch(Exception e) { e.printStackTrace(); } }
        }

        return result;
    }

    // Int 형 반환 시 DAO에서 호출할 메소드
    @Override
    public int executeQueryOneInt(final String sql, Object ...param) throws SQLException {        
        int result = queryStrategyContext(
            new StatementStrategy() {
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
            });
        
        return result;
    }

}