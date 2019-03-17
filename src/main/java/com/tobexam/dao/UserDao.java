package com.tobexam.dao;

import java.util.*;
import com.tobexam.model.User;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

public interface UserDao {
    public void setDataSource(DataSource dataSource);
    public void deleteAll() throws Exception;
    public void add(User user) throws Exception;
    public void update(User user) throws Exception;
    public void delete(User user) throws Exception;
    public User get(String id) throws EmptyResultDataAccessException, Exception;
    public int count(String id) throws Exception;
    public int countAll() throws Exception;
    public List<User> selectAll() throws Exception;
}