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

public class UserDaoJdbc_Mod implements UserDao {
    private Context jdbcContext;
    private DataSource dataSource;

    public void setJdbcContext(Context jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();

        this.jdbcContext.setDataSource(dataSource);

        this.dataSource = dataSource;
    }

    public void deleteAll() {      
        try {
            this.jdbcContext.executeSql("Delete From USER");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // User Add
    // 내부 익명클래스에서 사용하려면 외부 인자는 final이어야 함
    public void add(final User user) {
        try {
            this.jdbcContext.executeSql("Insert Into USER(id, name, password, level, login, recommend, email) Values (?, ?, ?, ?, ?, ?) ", user.getId(), user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend(), user.getEmail());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(final User user) {
        try {
            this.jdbcContext.executeSql("Update USER set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? Where id = ? ", user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(final User user) {
        try {
            this.jdbcContext.executeSql("Delete From USER Where id = ?", user.getId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public int count(String id) {
        int count = 0;

        try {
            count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER Where id = ? ", id);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int countAll() {
        int count = 0;

        try {
            count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER");
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }

    public User get(String id) {
        User user = null;

        try {
            user = this.jdbcContext.executeQueryOneObject("Select id, name, password, level, login, recommend, email From USER Where id = ? ", User.class, id);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<User> selectAll() {

        List<User> userList = null;
        
        try {
            userList = this.jdbcContext.executeQueryList("Select id, name, password, level, login, recommend, email From USER", User.class);            
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }
}