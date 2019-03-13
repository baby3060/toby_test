package com.tobexam.dao;

import com.tobexam.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 카운트만 증가시키고, 실제로 ConnectionMaker을 반환하는 것은 realConnectionMaker
 */
public class CountingConnectionMaker implements ConnectionMaker {
    private int counter;
    // 실제로 만들어서 반환하는 ConnectionMaker
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        this.counter++;

        return realConnectionMaker.getConnection();
    }

    public int getCounter() {
        return this.counter;
    }
}