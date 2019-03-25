package com.tobexam.common;

public class ConnectionBean {
    private String className;
    private String host;
    private String databaseName;
    private String userName;
    private String userPass;

    public ConnectionBean() {
        
    }

    public ConnectionBean(String className, String host, String databaseName, String userName, String userPass) {
        this.className = className;
        this.host = host;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPass = userPass;
    }

    public String getConnStr() {
        String connectionStr = String.format("%s%s%s", this.host, this.databaseName, "?characterEncoding=UTF-8");

        return connectionStr;
    }

    public String getClassName() {
        return this.className;
    }

    public String getHost() {
        return this.host;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    

}