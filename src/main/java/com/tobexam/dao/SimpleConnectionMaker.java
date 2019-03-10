package com.tobexam.dao;

import com.tobexam.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.tobexam.common.*;

public class SimpleConnectionMaker {
    private ConnectionBean connBean;
    
    public SimpleConnectionMaker(ConnectionBean connBean) {
        this.connBean = connBean;   
    }
    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());

        Class.forName(this.connBean.getClassName());

        conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

        return conn;
    }

}