package com.tobexam;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.*;

import com.tobexam.model.*;
import com.tobexam.dao.*;
import org.junit.runner.RunWith;


public class UserTest {
    
    User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    // 다음 레벨과 비교
    public void upgradeLevel() {
        Level[] levels = Level.values();

        for(Level level : levels) {
            if(level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(), is(level.nextLevel()));
        }
    }

    @Test(expected=IllegalStateException.class)
    public void cannotUpgradeLevel() {  
        Level[] levels = Level.values();
        for(Level level : levels) {
            if(level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
    
}
