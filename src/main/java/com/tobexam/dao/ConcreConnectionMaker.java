package com.tobexam.dao;

import com.tobexam.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.tobexam.common.*;

public class ConcreConnectionMaker implements ConnectionMaker {
    private ConnectionBean connBean;

    public ConcreConnectionMaker() {
        
    }

    public ConcreConnectionMaker(ConnectionBean connBean) {
        this.connBean = connBean;
    }

    public void setConnBean(ConnectionBean connBean) {
        this.connBean = connBean;
    }

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;

        String connectionStr = String.format("%s%s", this.connBean.getHost(), this.connBean.getDatabaseName());

        Class.forName(this.connBean.getClassName());

        conn = DriverManager.getConnection(connectionStr, this.connBean.getUserName(), this.connBean.getUserPass());

        return conn;
    }

}