package com.tobexam.dao;

import com.tobexam.model.*;

import java.util.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

/**
 * 스프링의 JdbcTemplate 사용
 */
public class UserDaoJdbc_Template implements UserDao {

    // 받아오는 UserMapper 중복 제거
    private RowMapper<User> userMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            return user;
        }
    };
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void deleteAll() throws Exception {      
        // 내장 콜백 미사용
        /*
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("Delete From USER");
            }
        });
        */
        // 내장 콜백 사용
        this.jdbcTemplate.update("Delete From USER");
    }

    // User Add
    // 내부 익명클래스에서 사용하려면 외부 인자는 final이어야 함
    public void add(final User user) throws Exception {
        this.jdbcTemplate.update("Insert Into USER(id, name, password, level, login, recommend) Values (?, ?, ?, ?, ?, ?) ", user.getId(), user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend());
    }

    public void update(final User user) throws Exception {
        this.jdbcTemplate.update("Update USER set name = ?, password = ?, level = ?, login = ?, recommend = ? Where id = ? ", user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend(), user.getId());
    }

    public void delete(final User user) throws Exception {
        this.jdbcTemplate.update("Delete From USER Where id = ?", user.getId());
    }

    public User get(String id) throws EmptyResultDataAccessException, Exception {

        // 바로 아래 Count와의 차이는 Integer.class인지 RowMapper<User>인가의 차이
        // RowMapper에서는 rs.next를 호출할 필요가 없다.
        // 행이 없을 경우 자동으로 EmptyResultDataAccessException을 던져준다.
        return this.jdbcTemplate.queryForObject("Select * From USER Where id = ? "
            , new Object[] { id }
            , this.userMapper
        );
    }

    public int count(String id) throws Exception {
        // 원래는 queryForInt가 있었으나 deprecated되서 아래와 같은 방법으로
        return this.jdbcTemplate.queryForObject("Select Count(*) As cnt From USER Where id = ? ", new Object[] { id }, Integer.class);
    }

    public int countAll() throws Exception {
        // 인터페이스 직접 적용(구체적인 반환 타입)
        /*
        return this.jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("Select Count(*) As cnt From USER");
            }
        }, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });
        */
        // 원래는 queryForInt가 있었으나 deprecated되서 아래와 같은 방법으로
        return this.jdbcTemplate.queryForObject("Select Count(*) As cnt From USER", new Object[] { }, Integer.class);
    }

    public List<User> selectAll() throws Exception {
        // query는 기본 리턴 타입이 List이다.
        return this.jdbcTemplate.query("Select * From USER Order By id "
            , new Object[] {  }
            , this.userMapper
            
        );
    }

    
}