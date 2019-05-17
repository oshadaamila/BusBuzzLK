package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crystalit.busbuzzlk.Database.Dao.UserDao;

public class SignInViewModel extends AndroidViewModel {

    UserDao mUserDao;

    public SignInViewModel(@NonNull Application application) {

        super(application);
        mUserDao = new UserDao();
    }

    public void signInUser(String userName, String password, ProgressDialog pd) {
        mUserDao.getUserByUsername(userName, pd);

    }
}
