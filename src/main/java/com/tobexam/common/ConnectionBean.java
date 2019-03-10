package com.tobexam.common;

public class ConnectionBean {
    private String className;
    private String host;
    private String databaseName;
    private String userName;
    private String userPass;

    public ConnectionBean(final String className, final String host, final String databaseName, final String userName, final String userPass) {
        this.className = className;
        this.host = host;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPass = userPass;
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
}