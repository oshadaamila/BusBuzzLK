package com.crystalit.busbuzzlk.Database.Dao;

import android.app.ProgressDialog;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.Database.ValueListeners.UserValueLIstener;
import com.crystalit.busbuzzlk.models.User;

public class UserDao {

    private Database mDatabase;

    public UserDao() {
        mDatabase = Database.getInstance();
    }

    public void insertUser(User user) {
        mDatabase.getUsersReference().child(user.getuName()).setValue(user);
    }

    public void getUserByUsername(String userName, ProgressDialog pd) {
        //String userName;
        UserValueLIstener userValueLIstener = new UserValueLIstener(pd);
        mDatabase.getUsersReference().child(userName).addListenerForSingleValueEvent(userValueLIstener);
    }
}
