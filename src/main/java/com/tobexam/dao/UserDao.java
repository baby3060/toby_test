package com.tobexam.dao;

import java.util.*;
import com.tobexam.model.User;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

public interface UserDao {
    public void setDataSource(DataSource dataSource);
    public void deleteAll();
    public void add(User user);
    public void update(User user);
    public void delete(User user);
    public User get(String id);
    public int count(String id);
    public int countAll();
    public List<User> selectAll();
}