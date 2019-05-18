package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.crystalit.busbuzzlk.Database.Dao.UserDao;
import com.crystalit.busbuzzlk.models.User;

public class SignInViewModel extends AndroidViewModel {

    UserDao mUserDao;
    String enteredPassword;

    public SignInViewModel(@NonNull Application application) {

        super(application);
        mUserDao = new UserDao();
    }

    // This method is called in the user value listener
    public void signInUser(User user) {
        if (user != null) {
            if (enteredPassword.equals(user.getPassword())) {
                // TODO save the user and procees to next steps
            } else {
                Toast.makeText(getApplication(), "Password incorrect", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplication(), "User does not exists, create a user account to " +
                    "proceed", Toast.LENGTH_LONG).show();
        }

    }

    public void getUser(String userName, String password, ProgressDialog pd) {
        this.enteredPassword = password;
        mUserDao.getUserByUsername(userName, pd, this);
    }
}
