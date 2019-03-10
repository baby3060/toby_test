package com.tobexam.dao;

import java.sql.SQLException;

import com.tobexam.common.*;

public class NUserDao extends UserDao {
    private ConnectionBean connBean;
    
    public NUserDao(ConnectionBean connBean) {
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