package com.tobexam.dao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Connection;

import java.util.*;
import java.lang.reflect.*;

public class JdbcContext implements Context {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 전략을 입력받아서 executeUpdate를 행하는 메소드
    // try ~ catch ~ finally 부분을 분리하였다.
    // 전략 실행 메소드
    private void updateStrategyContext(StatementStrategy strategy) throws SQLException {
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

    // executeUpdate문 전용 pstmt 전략 메소드
    public void executeSql(final String sql, Object ...param) throws SQLException {
        updateStrategyContext(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                int index = 1;

                PreparedStatement pstmt = connection.prepareStatement(sql);

                setPstmt(pstmt, param);

                return pstmt;
            }
        });
    }

    // 값 반환 템플릿
    private int queryStrategyContext(StatementStrategy stateStrategy) throws SQLException {
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

    @Override
    // int형 조회 전용 pstmt 전략 메소드
    public int executeQueryOneInt(final String sql, Object ...param) throws SQLException {        
        int result = queryStrategyContext(
            new StatementStrategy() {
                public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    
                    setPstmt(pstmt, param);

                    return pstmt;
                }
            });
        
        return result;
    }

    // get 전략 및 반환 메소드
    public <T extends Object> T executeQueryOneObject(final String sql, Class<T> type, Object ...param) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        T obj = null;

        try {
            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            setPstmt(pstmt, param);

            rs = pstmt.executeQuery();

            Class clazz = Class.forName(type.getName()); 

            if( rs.next() ) {
                
                obj = (T)clazz.newInstance();

                setResultSet(obj, clazz, rs);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rs != null) { try { rs.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(pstmt != null) { try { pstmt.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(conn != null) { try { conn.close(); } catch(Exception e) { e.printStackTrace(); } }
        }
        
        return obj;
    }

    // selectAll 전략 및 반환 메소드
    public <T extends Object> List<T> executeQueryList(final String sql, Class<T> type, Object ...param) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        T obj = null;
        List<T> list = new ArrayList<T>();

        try {
            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            setPstmt(pstmt, param);

            rs = pstmt.executeQuery();

            Class clazz = Class.forName(type.getName()); 

            while( rs.next() ) {

                obj = (T)clazz.newInstance();

                setResultSet(obj, clazz, rs);

                list.add(obj);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rs != null) { try { rs.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(pstmt != null) { try { pstmt.close(); } catch(Exception e) { e.printStackTrace(); } }
            if(conn != null) { try { conn.close(); } catch(Exception e) { e.printStackTrace(); } }
        }
        
        return list;
    }


    // PSTMT 매개변수 정리 메소드
    private void setPstmt(PreparedStatement pstmt, Object ...param) throws SQLException {
        int index = 1;

        if( param.length > 0 ) {
            for(Object paramObj : param) {
                if( paramObj instanceof Integer ) {
                    pstmt.setInt(index, (Integer)paramObj);
                } else if( paramObj instanceof String ) {
                    pstmt.setString(index, (String)paramObj);
                } else if( paramObj instanceof Double ) {
                    pstmt.setDouble(index, (Double)paramObj);
                } else if( paramObj instanceof Float ) {
                    pstmt.setFloat(index, (Float)paramObj);
                } else if( paramObj instanceof Long ) {
                    pstmt.setLong(index, (Long)paramObj);
                }
                index++;
            }
        }
    }

    public <T extends Object> void setResultSet(T obj, Class clazz, ResultSet rs) throws SQLException {
        Object inpVal = new Object();
        String colName = "";

        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if( method.getName().startsWith("set") ) {
                    colName = method.getName().substring(3).toLowerCase();
        
                    for (Class tmpClass : method.getParameterTypes()) {
                        
                        if( tmpClass.cast(tmpClass.newInstance()) instanceof String ) {
                            inpVal = rs.getString(colName);
                            break;
                        } else if( tmpClass.cast(tmpClass.newInstance()) instanceof Integer ) {
                            inpVal = new Integer(rs.getInt(colName));
                            break;
                        } else if(tmpClass.cast(tmpClass.newInstance()) instanceof Long) {
                            inpVal = new Long(rs.getLong(colName));
                            break;
                        } 
                    }
                    method.invoke(obj, inpVal);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    

}