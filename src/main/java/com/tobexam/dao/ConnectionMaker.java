package com.tobexam.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.tobexam.common.*;

public interface ConnectionMaker {
    public Connection getConnection() throws ClassNotFoundException, SQLException;
}