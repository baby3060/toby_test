package com.tobexam;

import com.tobexam.model.*;
import com.tobexam.dao.*;

public class App {
    public static void main( String[] args ) {
        User user = new User();
        
        UserDao dao = new UserDao();

        try {
            user = dao.get("111");

            System.out.println(user);

            int count = dao.countAll();

            System.out.println("All Count : " + count);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
