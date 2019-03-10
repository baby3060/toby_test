package com.tobexam.dao;

import com.tobexam.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.tobexam.common.*;

public class UserDao {
    private ConnectionBean connBean;
    
    public UserDao(ConnectionBean connBean) {
        this.connBean = connBean;   
    }

    // User Add
    public int add(User user) throws Exception {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());

        try {
            Class.forName(this.connBean.getClassName());

            String sql = "Insert Into USER(id, name, password) Values (?, ?, ?) ";

            conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

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
        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());

        try {
            Class.forName(this.connBean.getClassName());

            String sql = "Select id, name, password From USER Where id = ? ";

            conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

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

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());
        try {
            Class.forName(this.connBean.getClassName());

            String sql = "Select Count(*) As cnt From USER";

            conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

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

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());

        try {
            Class.forName(this.connBean.getClassName());

            String sql = "Delete From USER Where id = ? ";

            conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

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

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());
        try {
            Class.forName(this.connBean.getClassName());

            String sql = "Delete From USER ";

            conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

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