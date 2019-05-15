package com.crystalit.busbuzzlk.ViewModels;

import android.arch.lifecycle.ViewModel;

import com.crystalit.busbuzzlk.Database.Dao.UserDao;
import com.crystalit.busbuzzlk.models.User;

public class SignUpViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    UserDao mUserDao;

    public SignUpViewModel() {
        mUserDao = new UserDao();
    }

    public void signUpUser(String uname, String email, String password) {
        User user = new User(uname, email, password);
        mUserDao.insertUser(user);

    }
}
