package com.tobexam.service;

import java.util.*;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

public interface UserService {
    public void upgradeLevels();
    public void add(User user);
    public void deleteAll();

    public void update(User user);
    public void delete(User user);
    public User get(String id);
    public int count(String id);
    public int countAll();
    public List<User> selectAll();

    public void updateRecommend(User user, String recommendId);
}