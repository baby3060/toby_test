package com.tobexam.sqlconfig;

import java.util.*;

import com.tobexam.sqlconfig.jaxb.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.annotation.PostConstruct;

public class DefaultSqlService extends BaseSqlService {
    public DefaultSqlService() {
        setSqlReader(new JaxbXmlSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
    }
}