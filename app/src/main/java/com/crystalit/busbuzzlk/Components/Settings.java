package com.crystalit.busbuzzlk.Components;

//This class can be used to save/retrieve app settings

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private Context mContext;
    private int LOGGED_IN = 1;
    SharedPreferences sharedPref;

    public Settings(Context context){
        this.mContext =  context;
        sharedPref = mContext.getSharedPreferences("com.crystalit.busbuzzlk" +
                ".settings",Context.MODE_PRIVATE);
    }

    public void setUserLogged(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LOGGED_IN", LOGGED_IN);
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

}
