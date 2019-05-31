package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.crystalit.busbuzzlk.Components.Settings;
import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Database.Dao.UserDao;
import com.crystalit.busbuzzlk.Views.HomeNavigationActivity;
import com.crystalit.busbuzzlk.models.User;

public class SignInViewModel extends AndroidViewModel {

    UserDao mUserDao;
    String enteredPassword;
    UserManager userManager;

    public SignInViewModel(@NonNull Application application) {

        super(application);
        mUserDao = new UserDao();
        userManager = UserManager.getInstance();
    }

    // This method is called in the user value listener
    public void signInUser(User user) {
        if (user != null) {
            if (enteredPassword.equals(user.getPassword())) {
                userManager.loginUser(user,getApplication().getApplicationContext());
                Intent intent = new Intent(getApplication().getApplicationContext(),
                        HomeNavigationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplication().startActivity(intent);

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
