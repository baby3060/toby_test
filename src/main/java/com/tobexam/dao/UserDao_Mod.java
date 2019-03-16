package com.tobexam.dao;

import com.tobexam.model.User;

import java.util.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

public class UserDao_Mod {
    private Context jdbcContext;
    private DataSource dataSource;

    public void setJdbcContext(Context jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteAll() throws Exception {      
        this.jdbcContext.executeSql("Delete From USER");
    }

    // User Add
    // 내부 익명클래스에서 사용하려면 외부 인자는 final이어야 함
    public void add(final User user) throws Exception {
        this.jdbcContext.executeSql("Insert Into USER(id, name, password) Values (?, ?, ?) ", user.getId(), user.getName(), user.getPassword());
    }

    public void update(final User user) throws Exception {
        this.jdbcContext.executeSql("Update USER set name = ?, password = ? Where id = ? ", user.getName(), user.getPassword(), user.getId());
    }

    public void delete(final User user) throws Exception {
        this.jdbcContext.executeSql("Delete From USER Where id = ?", user.getId());
    }

    public User get(String id) throws EmptyResultDataAccessException, Exception {
        
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "Select id, name, password From USER Where id = ? ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }

            if( user == null ) {
                throw new EmptyResultDataAccessException(1);
            }
        } catch(SQLException e ) {
            throw new Exception(e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            if(conn != null) {
                try {
                    conn.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return user;
    }

    public int countAll() throws Exception {
        int count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER");

        return count;
    }

    public int countAll(String id) throws Exception {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "Select Count(*) As cnt From USER Where id = ? ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if( rs.next() ) {
                count = rs.getInt("cnt");
            }
        } catch(SQLException e ) {
            throw new Exception(e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            if(conn != null) {
                try {
                    conn.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return count;
    }

    public List<User> selectAll() throws Exception {
        List<User> userList = new ArrayList<User>();
        User user = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "Select id, name, password From USER ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            while( rs.next() ) {
                user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                userList.add(user);
            }

        } catch(SQLException e ) {
            throw new Exception(e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            if(conn != null) {
                try {
                    conn.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return userList;
    }
}