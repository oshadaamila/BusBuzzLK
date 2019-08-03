package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.location.Location;
import android.support.annotation.NonNull;

import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Database.Dao.BusDao;
import com.crystalit.busbuzzlk.Database.Dao.UserDao;
import com.google.android.gms.tasks.Task;

public class HomeNavigationViewModel extends AndroidViewModel {

    UserManager mUserManager;
    UserDao mUserDao;
    BusDao mBusDao;

    public HomeNavigationViewModel(@NonNull Application application) {
        super(application);
        mUserManager = UserManager.getInstance();
        mUserDao = new UserDao();
        mBusDao = new BusDao();
    }

    public void logOutUser(){
        mUserManager.logOutUser(getApplication().getApplicationContext());
    }

    //public Task<Location> updateMapOnCreate(){
      // return locationProvider.getLastLocation();
    //}

    public void updateLocationToDatabase(double latitude,double longitude,double bearing){
        UserManager.getInstance().getLoggedUser().setLocation(latitude,longitude,bearing);
        if (mUserManager.getLoggedUser().isInBus()){
            mUserDao.updateUserLocation(UserManager.getInstance().getLoggedUser(),latitude,longitude,
                    bearing);
            mBusDao.updateBusLocation(mUserManager.getCurrentBus(),latitude,longitude,bearing);


        }else{
            mUserDao.updateUserLocation(UserManager.getInstance().getLoggedUser(),latitude,longitude,
                    bearing);
        }


    }



}
