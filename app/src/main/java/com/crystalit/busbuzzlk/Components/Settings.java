package com.crystalit.busbuzzlk.Components;

//This class can be used to save/retrieve app settings

import android.content.Context;
import android.content.SharedPreferences;

import com.crystalit.busbuzzlk.models.User;

public class Settings {

    private Context mContext;
    private int LOGGED_IN = 1;
    private int LOGGED_OUT = 0;
    SharedPreferences sharedPref;

    public Settings(Context context){
        this.mContext =  context;
        sharedPref = mContext.getSharedPreferences("com.crystalit.busbuzzlk" +
                ".settings",Context.MODE_PRIVATE);
    }

    public void setUserLogged(User user){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LOGGED_IN", LOGGED_IN);
        editor.putString("LOGGED_UNAME",user.getuName());
        editor.putString("LOGGED_EMAIL",user.getEmail());
        editor.putString("LOGGED_PASSWORD",user.getPassword());
        editor.commit();
    }

    // check whether user has logged in before and return a boolean value
    public boolean isUserLogged(){
        int loggedIn = sharedPref.getInt("LOGGED_IN",0);
        if(loggedIn == LOGGED_IN){
            return true;
        }else{
            return false;
        }
    }

    public void logOutUser(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LOGGED_IN", LOGGED_OUT);
        editor.putString("LOGGED_UNAME","null");
        editor.putString("LOGGED_EMAIL","null");
        editor.putString("LOGGED_PASSWORD","null");
        editor.commit();
    }

    public User getLoggedUser(){
        String loggedUName = sharedPref.getString("LOGGED_UNAME","null");
        String loggedEmail = sharedPref.getString("LOGGED_EMAIL","null");
        String loggedPassword = sharedPref.getString("LOGGED_PASSWORD","null");
        User loggedUser = new User(loggedUName,loggedEmail,loggedPassword);
        return loggedUser;
    }



}
