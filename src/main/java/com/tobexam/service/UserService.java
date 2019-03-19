package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

public interface UserService {
    public void upgradeLevels() throws Exception;
    public void add(User user);
    public void deleteAll();
}