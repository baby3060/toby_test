package com.tobexam.sqlconfig;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

public class UserSqlMapConfig implements SqlMapConfig  {
    public Resource getSqlMapResource() {
        return new ClassPathResource("sqlmap.xml");
    }
}