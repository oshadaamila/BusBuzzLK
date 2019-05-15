package com.crystalit.busbuzzlk.Database.Dao;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.models.User;

public class UserDao {

    private Database mDatabase;

    public UserDao() {
        mDatabase = Database.getInstance();
    }

    public void insertUser(User user) {
        mDatabase.getUsersReference().child(user.getuName()).setValue(user);
    }
}
