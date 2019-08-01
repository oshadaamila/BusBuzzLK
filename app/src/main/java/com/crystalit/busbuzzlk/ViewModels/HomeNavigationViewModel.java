package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.location.Location;
import android.support.annotation.NonNull;

import com.crystalit.busbuzzlk.Components.Location.LocationProvider;
import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Database.Dao.UserDao;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;

public class HomeNavigationViewModel extends AndroidViewModel {

    UserManager mUserManager;
    LocationProvider locationProvider;
    UserDao mUserDao;

    public HomeNavigationViewModel(@NonNull Application application) {
        super(application);
        mUserManager = UserManager.getInstance();
        mUserManager.getLoggedUser().setInBus(true);
        mUserManager.getLoggedUser().setRouteNo("164");
        locationProvider = new LocationProvider(application.getApplicationContext());
        mUserDao = new UserDao();
    }

    public void logOutUser(){
        mUserManager.logOutUser(getApplication().getApplicationContext());
    }

    public Task<Location> updateMapOnCreate(){
       return locationProvider.getLastLocation();
    }

    public void updateLocationToDatabase(double latitude,double longitude,double bearing){
        UserManager.getInstance().getLoggedUser().setLocation(latitude,longitude);
        mUserDao.updateUserLocation(UserManager.getInstance().getLoggedUser(),latitude,longitude,
                bearing);

    }

}
