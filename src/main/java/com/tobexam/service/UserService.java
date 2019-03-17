package com.tobexam.service;

import com.tobexam.model.*;
import com.tobexam.dao.*;

import java.util.*;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        try {
            List<User> users = userDao.selectAll();

            System.out.println(users);

            boolean changed = false;

            for( User user : users ) {
                changed = false;

                if( user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                    user.setLevel(Level.SILVER);
                    changed = true;
                } else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                    user.setLevel(Level.GOLD);
                    changed = true;
                } else {
                    changed = false;
                }

                if( changed ) {
                    userDao.update(user);
                }
            }
        } catch(Exception e) {

        }
        
    }

    public void deleteAll() {
        try {
            userDao.deleteAll();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void add(User user) {
        try {
            if( user.getLevel() == null ) {
                user.setLevel(Level.BASIC);
            }
            userDao.add(user);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}