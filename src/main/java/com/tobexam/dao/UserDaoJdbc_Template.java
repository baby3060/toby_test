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
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;

/**
 * 스프링의 JdbcTemplate 사용
 */
public class UserDaoJdbc_Template implements UserDao {

    private String sqlAdd;
    private String sqlDeleteAll;
    private String sqlUpdate;
    private String sqlDelete;
    private String sqlGet;
    private String sqlCount;
    private String sqlCountAll;
    private String sqlSelectAll;

    public void setSqlAdd(String sqlAdd) {
        this.sqlAdd = sqlAdd;
    }

    public void setSqlDeleteAll(String sqlDeleteAll) {
        this.sqlDeleteAll = sqlDeleteAll;
    }

    public void setSqlUpdate(String sqlUpdate) {
        this.sqlUpdate = sqlUpdate;
    }

    public void setSqlDelete(String sqlDelete) {
        this.sqlDelete = sqlDelete;
    }


    public void setSqlGet(String sqlGet) {
        this.sqlGet = sqlGet;
    }

    public void setSqlCount(String sqlCount) {
        this.sqlCount = sqlCount;
    }

    public void setSqlCountAll(String sqlCountAll) {
        this.sqlCountAll = sqlCountAll;
    }

    public void setSqlSelectAll(String sqlSelectAll) {
        this.sqlSelectAll = sqlSelectAll;
    }


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
            user.setEmail(rs.getString("email"));
            return user;
        }
    };
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void deleteAll() {      
        // 내장 콜백 미사용
        /*
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("Delete From USER");
            }
        });
        */
        // 내장 콜백 사용
        this.jdbcTemplate.update(this.sqlDeleteAll);
    }

    // User Add
    // 내부 익명클래스에서 사용하려면 외부 인자는 final이어야 함
    public void add(final User user) throws DuplicateKeyException {
        this.jdbcTemplate.update(this.sqlAdd, user.getId(), user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public void update(final User user) {
        this.jdbcTemplate.update(this.sqlUpdate, user.getName(), user.getPassword(), user.getLevel().getValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }

    public void delete(final User user) {
        this.jdbcTemplate.update(this.sqlDelete, user.getId());
    }

    public User get(String id) {

        // 바로 아래 Count와의 차이는 Integer.class인지 RowMapper<User>인가의 차이
        // RowMapper에서는 rs.next를 호출할 필요가 없다.
        // 행이 없을 경우 자동으로 EmptyResultDataAccessException을 던져준다.
        return this.jdbcTemplate.queryForObject(this.sqlGet
            , new Object[] { id }
            , this.userMapper
        );
    }

    public int count(String id) {
        // 원래는 queryForInt가 있었으나 deprecated되서 아래와 같은 방법으로
        return this.jdbcTemplate.queryForObject(this.sqlCount, new Object[] { id }, Integer.class);
    }

    public int countAll() {
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
        return this.jdbcTemplate.queryForObject(this.sqlCountAll, new Object[] { }, Integer.class);
    }

    public List<User> selectAll() {
        // query는 기본 리턴 타입이 List이다.
        return this.jdbcTemplate.query(this.sqlSelectAll
            , new Object[] {  }
            , this.userMapper
            
        );
    }

    
}