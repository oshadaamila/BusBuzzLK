package com.crystalit.busbuzzlk.Components;

import android.content.Context;

import com.crystalit.busbuzzlk.models.Bus;
import com.crystalit.busbuzzlk.models.User;

public class UserManager {

    private static UserManager instance;
    private Bus currentBus;
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
        settings.setUserLogged(user);
    }

    public void logOutUser(Context context){
        this.loggedUser = null;
        Settings settings = new Settings(context);
        settings.logOutUser();

    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void addUserToaBus(String routeNo){
        BusManager busManager = new BusManager();
        busManager.addUserToBus(loggedUser.getuName(),routeNo,loggedUser.getLatitude(),loggedUser
                .getLongitude());

    }

    public void setCurrentBus(Bus bus){
        this.currentBus = bus;
        loggedUser.setInBus(true);
        loggedUser.setRouteNo(bus.getRouteID());
    }

    public Bus getCurrentBus(){
        return currentBus;
    }

    //remove currentbus and set inbus to false after removing data from the database
    public void removeUserFromBus() {
        String busId = currentBus.getId();
        BusManager busManager = new BusManager();
        busManager.removeUserFromBus(busId, UserManager.getInstance().loggedUser.getuName());
        currentBus = null;
        UserManager.getInstance().getLoggedUser().setInBus(false);
        UserManager.getInstance().getLoggedUser().setRouteNo("0");

    }








}
