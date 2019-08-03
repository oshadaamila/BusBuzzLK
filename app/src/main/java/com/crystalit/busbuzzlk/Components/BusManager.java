package com.crystalit.busbuzzlk.Components;

import android.util.Log;

import com.crystalit.busbuzzlk.Database.Dao.BusDao;
import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.models.Bus;
import com.crystalit.busbuzzlk.models.User;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusManager {

    BusDao busDao;

    public BusManager() {
        busDao = new BusDao();
    }

    public void getBusesWithinRange(final Double latitude, final Double longitude, final String userId, final String routeNo) {
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
                selectTheBus(latitude, longitude, busList, userId, routeNo);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("geoquery error", error.toString());
            }
        });
    }

    public void addUserToBus(String userId, String routeNo, Double lattiude, Double longitude) {
        getBusesWithinRange(lattiude, longitude, userId, routeNo);
    }

    private void selectTheBus(Double latitude, Double longitude, List<Bus> busList, String userId,
                              String routeNo) {
        Log.d("after geoquery","Buses Received"+Integer.toString(busList.size()));

        if (busList.size() == 0 && busList != null) {
            //create a new bus
            Bus bus = createNewBus(latitude, longitude, routeNo);
            UserManager.getInstance().setCurrentBus(bus);
            UserManager.getInstance().getLoggedUser().setInBus(true);
            UserManager.getInstance().getLoggedUser().setRouteNo(routeNo);
            //this will add new bus to the database
            busDao.addNewBusToDatabase(bus);
            //register the bus in usermanager
            UserManager.getInstance().setCurrentBus(bus);

        } else if (busList.size() > 0) {
            int a  = busList.size();
            int x =0;
        } else {
            Log.e("error at geo_fire", "getBusesWithinRange return a null list");
        }
    }

    private Bus createNewBus(Double latitude, Double longitude, String routeNo) {
        String busId = createNewBusId();
        Bus bus = new Bus(busId, latitude, longitude, routeNo);
        return bus;
    }

    //current timestamp will be created as bus_id
    private String createNewBusId() {
        Date date = new Date();
        long timeStamp = date.getTime();
        return Long.toString(timeStamp);
    }
}
