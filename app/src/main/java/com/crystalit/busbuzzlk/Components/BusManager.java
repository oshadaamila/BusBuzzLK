package com.crystalit.busbuzzlk.Components;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.crystalit.busbuzzlk.Database.Dao.BusDao;
import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.Fragments.SelectBusDialog;
import com.crystalit.busbuzzlk.models.Bus;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusManager {

    BusDao busDao;
    List<Bus> nearestBusesFromFirebase;
    int iter = 0;
    boolean userAssignedToBus = false;

    public BusManager() {
        busDao = new BusDao();
        this.nearestBusesFromFirebase = new ArrayList<Bus>();

    }

    private void getBusesWithinRange(final Double latitude, final Double longitude, final String
            userId, final FragmentManager fm, final ProgressDialog pd) {
        final List<String> busList = new ArrayList<String>();

        GeoQuery geoQuery = Database.getInstance().getGeoBusInstance().queryAtLocation(new
                GeoLocation(latitude, longitude), 0.01);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Bus bus = new Bus(key,location.latitude,location.longitude);
                busList.add(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                selectTheBus(latitude, longitude, busList, userId, fm, pd);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("geoquery error", error.toString());
            }
        });
    }

    public void addUserToBus(String userId, FragmentManager fm, Double lattiude, Double
            longitude, ProgressDialog pd) {
        userAssignedToBus = false;
        getBusesWithinRange(lattiude, longitude, userId, fm, pd);


    }

    private void selectTheBus(Double latitude, Double longitude, List<String> busList, String
            userId, FragmentManager fm, ProgressDialog pd) {
        Log.d("tagfordebug", "Buses Received" + Integer.toString(busList.size()));

//        if (busList.size() == 0 && busList != null) {
//            //create a new bus
////            Bus bus = createNewBus(latitude, longitude, routeNo);
////            //this will add new bus to the database
////            busDao.addNewBusToDatabase(bus);
////            //register the bus in usermanager
////            UserManager.getInstance().setCurrentBus(bus);
////            UserManager.getInstance().getLoggedUser().setInBus(true);
////            UserManager.getInstance().getLoggedUser().setRouteNo(routeNo);
////            userAssignedToBus = true;
//            pd.dismiss();
//            FragmentTransaction ft = fm.beginTransaction();
//            Fragment prev = fm.findFragmentByTag("dialog");
//            if (prev != null) {
//                ft.remove(prev);
//            }
//            ft.addToBackStack(null);
//            DialogFragment dialogFragment = new SelectBusDialog(nearestBusesFromFirebase);
//            dialogFragment.show(ft, "dialog");
//
//
//        } else if (busList.size() > 0) {
//
//            getBusesFromFireBase(busList, fm,pd);
        pd.dismiss();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new SelectBusDialog(busList);
        dialogFragment.show(ft, "dialog");


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

    private void getBusesFromFireBase(final List<Bus> busKeysFromGeoFire, final FragmentManager
            fm, final ProgressDialog pd) {
        iter = 0;
        for (int i = 0; i < busKeysFromGeoFire.size(); i++) {
            iter = i;
            Bus bus = busKeysFromGeoFire.get(i);
            String key = bus.getId();
            Log.d("tagfordebug", "getBusesFromFireBase:" + key);
            Log.d("tagfordebug", "iteration: :" + Integer.toString(iter));
            DatabaseReference ref = Database.getInstance().getBusReference().child(key);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.child("id").getValue().toString();
                    String lat = dataSnapshot.child("latitude").getValue().toString();
                    String lng = dataSnapshot.child("longitude").getValue().toString();
                    String routeId = dataSnapshot.child("routeID").getValue().toString();
                    List<String> travellers = new ArrayList<String>();
                    Iterable<DataSnapshot> ds = dataSnapshot.child("travellers").getChildren();
                    for (DataSnapshot child : ds) {
                        travellers.add(child.getKey());
                    }
                    String bearing = dataSnapshot.child("travellers").child(travellers.get(0))
                            .child("bearing").getValue().toString();
                    Bus bus = new Bus(id, Double.parseDouble(lat), Double.parseDouble(lng),
                            routeId);
                    bus.setBearing(Double.parseDouble(bearing));
                    boolean userInBus = userInTheGivenBus(bus, bearing);
                    if (userInBus) {
                        Log.d("tagfordebug", "User in bus" + Integer.toString(iter));
                        nearestBusesFromFirebase.add(bus);
//                        busDao.registerTravellerToBus(bus.getId(), UserManager.getInstance()
//                                .getLoggedUser().getuName(), UserManager.getInstance()
//                                .getLoggedUser().getBearing());
//                        UserManager.getInstance().setCurrentBus(bus);
//                        UserManager.getInstance().getLoggedUser().setInBus(true);
//                        UserManager.getInstance().getLoggedUser().setRouteNo(bus.getRouteID());
//                        userAssignedToBus = true;

                    } else if (iter == (busKeysFromGeoFire.size() - 1)) {
                        //all bus checked but no bus assigned
                        //create a new bus
//                        Bus newbus = createNewBus(UserManager.getInstance().getLoggedUser()
//                                        .getLatitude(),
//                                UserManager.getInstance().getLoggedUser().getLongitude(),
//                                currentRouteNo);
//                        //this will add new bus to the database
//                        busDao.addNewBusToDatabase(newbus);
//                        //register the bus in usermanager
//                        UserManager.getInstance().setCurrentBus(newbus);
//                        UserManager.getInstance().getLoggedUser().setInBus(true);
//                        UserManager.getInstance().getLoggedUser().setRouteNo(currentRouteNo);
//                        userAssignedToBus = true;
                        Log.d("tagfordebug", "iteration_ended");
                        Log.d("tagfordebug", "nearest_buses:" + Integer.toString
                                (nearestBusesFromFirebase.size()));
                        pd.dismiss();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment prev = fm.findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        //DialogFragment dialogFragment = new SelectBusDialog
                        // (nearestBusesFromFirebase);
                        //dialogFragment.show(ft, "dialog");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }


    private boolean userInTheGivenBus(Bus bus, String bearing) {
        Double bus_lat = bus.getLatitude();
        Double bus_lng = bus.getLongitude();
        Double bus_bearing = Double.parseDouble(bearing);
        Double user_lat = UserManager.getInstance().getLoggedUser().getLatitude();
        Double user_lng = UserManager.getInstance().getLoggedUser().getLongitude();
        Double user_bearing = UserManager.getInstance().getLoggedUser().getBearing();
        return isLatLangsWithingRange(new LatLng(bus_lat, bus_lng), new LatLng(user_lat, user_lng),
                    bus_bearing);
    }

    //length,width of a bus is taken as 14,2.5 metres
    private boolean isLatLangsWithingRange(LatLng bus, LatLng user, Double bearing) {
        double bearin_rad = Math.toRadians(bearing);
        Double lng_range = 14 * Math.sin(bearin_rad) + 2.5 * Math.cos(bearin_rad);
        Double lat_range = 14 * Math.cos(bearin_rad) + 2.5 * Math.sin(bearin_rad);
        Double dist_between_lats = Math.abs(bus.latitude - user.latitude) * 111000;
        Double dist_between_lngs = Math.abs(bus.longitude - user.longitude) * 110000;
        return (dist_between_lngs <= lng_range) || (dist_between_lats <= lat_range);
    }

    //remove the current user from travellers
    public void removeUserFromBus(final String busId, String uName) {
        busDao.removeUserFromBus(busId, uName);
        Database database = Database.getInstance();
        DatabaseReference ref = database.getBusReference().child(busId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("tagfordebug", "onDataChange: removeUserFromBus");
                if (!dataSnapshot.hasChild("travellers")) {
                    busDao.removeBus(busId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
