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

public class UserDaoJdbc_Mod implements UserDao {
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

    public int count(String id) throws Exception {
        int count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER Where id = ? ", id);
        return count;
    }

    public int countAll() throws Exception {
        int count = this.jdbcContext.executeQueryOneInt("Select Count(*) As cnt From USER");
        return count;
    }

    public User get(String id) throws EmptyResultDataAccessException, Exception {
        User user = this.jdbcContext.executeQueryOneObject("Select id, name, password From USER Where id = ? ", User.class, id);
        return user;
    }

    public List<User> selectAll() throws Exception {
        List<User> userList = this.jdbcContext.executeQueryList("Select id, name, password From USER", User.class);
        return userList;
    }
}