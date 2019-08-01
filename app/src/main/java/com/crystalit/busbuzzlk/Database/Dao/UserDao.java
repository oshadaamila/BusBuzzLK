package com.crystalit.busbuzzlk.Database.Dao;

import android.app.ProgressDialog;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.Database.ValueListeners.UserValueLIstener;
import com.crystalit.busbuzzlk.ViewModels.SignInViewModel;
import com.crystalit.busbuzzlk.models.User;
import com.firebase.geofire.GeoLocation;

public class UserDao {

    private Database mDatabase;

    public UserDao() {
        mDatabase = Database.getInstance();
    }

    public void insertUser(User user) {
        mDatabase.getUsersReference().child(user.getuName()).setValue(user);
    }

    public void getUserByUsername(String userName, ProgressDialog pd, SignInViewModel viewModel) {
        //String userName;
        UserValueLIstener userValueLIstener = new UserValueLIstener(pd, viewModel);
        mDatabase.getUsersReference().child(userName).addListenerForSingleValueEvent(userValueLIstener);
    }

    public void updateUserLocation(User user,double latitude,double longitude,double bearing){
        mDatabase.getRootReference().child("user_locations").child(user.getuName()).child
                ("latitude")
                .setValue(latitude);
        mDatabase.getRootReference().child("user_locations").child(user.getuName()).child
                ("longitude")
                .setValue(longitude);
        mDatabase.getRootReference().child("user_locations").child(user.getuName()).child("bearing")
                .setValue(bearing);
        mDatabase.getRootReference().child("user_locations").child(user.getuName()).child
                ("in_a_bus")
                .setValue(user.isInBus());
        mDatabase.getRootReference().child("user_locations").child(user.getuName()).child("route")
                .setValue(user.getRouteNo());
        mDatabase.getGeoUserInstance().setLocation(user.getuName(),new GeoLocation(latitude,
                longitude));
    }

}
