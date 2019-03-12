package com.tobexam;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import com.tobexam.factory.*;

public class App {
    public static void main( String[] args ) {

        User user = new User();
        
        UserDao userDao = new DaoFactory().userDao();

        try {
            user = userDao.get("111");

            System.out.println(user);

            int count = userDao.countAll();

            System.out.println("All Count : " + count);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
