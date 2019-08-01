package com.crystalit.busbuzzlk.Components;

import android.util.Log;
import android.widget.Toast;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.models.Bus;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class BusManager {

    public BusManager() {
    }

    public void getBusesWithinRange(Double latitude,Double longitude){
        final List<Bus> busList = new ArrayList<Bus>();
        GeoQuery geoQuery = Database.getInstance().getGeoBusInstance().queryAtLocation(new
                GeoLocation(latitude,longitude),0.01);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Bus bus = new Bus(key,location.latitude,location.longitude);
                busList.add(bus);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                selectTheBus(busList);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void addUserToBus(String userId,String roteNo,Double lattiude, Double longitude) {
        getBusesWithinRange(lattiude,longitude);
    }

    private void selectTheBus(List<Bus> busList) {
        Log.d("after geoquery","Buses Received"+Integer.toString(busList.size()));
    }
}
