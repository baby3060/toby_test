package com.tobexam.sqlconfig;

public class SqlretrievalFailureException extends RuntimeException {
    public SqlretrievalFailureException(String message) {
        super(message);
    }

    public SqlretrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}