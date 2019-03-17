package com.tobexam.dao;

import com.tobexam.model.*;

import java.util.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

public class UserDaoJdbc implements UserDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    // User Add
    public void add(User user) throws Exception {        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Insert Into USER(id, name, password, level, login, recommend) Values (?, ?, ?, ?, ?, ?) ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getLevel().getValue());
            pstmt.setInt(5, user.getLogin());
            pstmt.setInt(6, user.getRecommend());

            pstmt.executeUpdate();

        } catch(SQLException e ) {
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
    }

    public void update(User user) throws Exception {        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Update USER set name = ?, password = ?, level = ?, login = ?, recommend = ? Where id = ? ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getLevel().getValue());
            pstmt.setInt(4, user.getLogin());
            pstmt.setInt(5, user.getRecommend());

            pstmt.setString(6, user.getId());

            pstmt.executeUpdate();

        } catch(SQLException e ) {
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
    }

    public void delete(User user) throws Exception {        
        this.delete(user.getId());
    }

    public User get(String id) throws EmptyResultDataAccessException, Exception {
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "Select id, name, password, level, login, recommend From USER Where id = ? ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setLevel(Level.valueOf(rs.getInt("level")));
                user.setLogin(rs.getInt("login"));
                user.setRecommend(rs.getInt("recommend"));
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
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "Select Count(*) As cnt From USER";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

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

    public int count(String id) throws Exception {
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

    public int delete(String id) throws Exception {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Delete From USER Where id = ? ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            result = pstmt.executeUpdate();

        } catch(SQLException e ) {
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

    public void deleteAll() throws Exception {        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "Delete From USER ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.executeUpdate();
        } catch(SQLException e ) {
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
    }

    public List<User> selectAll() throws Exception {
        List<User> userList = new ArrayList<User>();
        User user = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "Select id, name, password, level, login, recommend From USER ";

            conn = dataSource.getConnection();

            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            while( rs.next() ) {
                user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setLevel(Level.valueOf(rs.getInt("level")));
                user.setLogin(rs.getInt("login"));
                user.setRecommend(rs.getInt("recommend"));
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