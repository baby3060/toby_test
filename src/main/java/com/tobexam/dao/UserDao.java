package com.tobexam.dao;

import com.tobexam.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    private ConnectionMaker connectionMaker;
    
    public UserDao() {
        
    }

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;   
    }

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    // User Add
    public int add(User user) throws Exception {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Insert Into USER(id, name, password) Values (?, ?, ?) ";

            conn = connectionMaker.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());

            result = pstmt.executeUpdate();

        } catch(ClassNotFoundException | SQLException e ) {
            throw new Exception(e);
        } finally {
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

        return result;
    }

    public User get(String id) throws Exception {
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "Select id, name, password From USER Where id = ? ";

            conn = connectionMaker.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }
        } catch(ClassNotFoundException | SQLException e ) {
            throw new Exception(e);
        } finally {
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
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "Select Count(*) As cnt From USER";

            conn = connectionMaker.getConnection();

            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                count = rs.getInt("cnt");
            }
        } catch(ClassNotFoundException | SQLException e ) {
            throw new Exception(e);
        } finally {
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

    public int delete(String id) throws Exception {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Delete From USER Where id = ? ";

            conn = connectionMaker.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            result = pstmt.executeUpdate();

        } catch(ClassNotFoundException | SQLException e ) {
            throw new Exception(e);
        } finally {
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

        return result;
    }

    public int deleteAll() throws Exception {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Delete From USER ";

            conn = connectionMaker.getConnection();

            pstmt = conn.prepareStatement(sql);

            result = pstmt.executeUpdate();

        } catch(ClassNotFoundException | SQLException e ) {
            throw new Exception(e);
        } finally {
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

        return result;
    }

}