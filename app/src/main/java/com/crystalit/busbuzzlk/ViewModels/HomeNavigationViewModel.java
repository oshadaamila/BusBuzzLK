package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crystalit.busbuzzlk.Components.UserManager;

public class HomeNavigationViewModel extends AndroidViewModel {

    UserManager mUserManager;

    public HomeNavigationViewModel(@NonNull Application application) {
        super(application);
        mUserManager = UserManager.getInstance();
    }

    public void logOutUser(){
        mUserManager.logOutUser(getApplication().getApplicationContext());
    }
}
