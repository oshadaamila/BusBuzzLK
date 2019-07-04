package com.crystalit.busbuzzlk.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.location.Location;
import android.support.annotation.NonNull;

import com.crystalit.busbuzzlk.Components.Location.LocationProvider;
import com.crystalit.busbuzzlk.Components.UserManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;

public class HomeNavigationViewModel extends AndroidViewModel {

    UserManager mUserManager;
    LocationProvider locationProvider;

    public HomeNavigationViewModel(@NonNull Application application) {
        super(application);
        mUserManager = UserManager.getInstance();
        locationProvider = new LocationProvider(application.getApplicationContext());
    }

    public void logOutUser(){
        mUserManager.logOutUser(getApplication().getApplicationContext());
    }

    public Task<Location> updateMapOnCreate(){
       return locationProvider.getLastLocation();
    }
}
