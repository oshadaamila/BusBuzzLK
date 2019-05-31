package com.crystalit.busbuzzlk.Components;

import android.content.Context;

import com.crystalit.busbuzzlk.models.User;

public class UserManager {

    private static UserManager instance;
    private User loggedUser;

    private UserManager(){

    }

    public static UserManager getInstance(){
        if (instance==null){
            instance = new UserManager();
        }

        return instance;
    }

    // login the given user and save it to the phone memory
    public void loginUser(User user,Context context){
        this.loggedUser = user;
        Settings settings = new Settings(context);
        settings.setUserLogged();
    }

    public User getLoggedUser(){
        return loggedUser;
    }


}