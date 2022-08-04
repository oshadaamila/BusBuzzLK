package com.crystalit.busbuzzlk.Database;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private static Database instance;

    private FirebaseDatabase firebaseDatabase;
    private GeoFire geoUserInstance;
    private GeoFire geoBusInstance;

    private Database() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        geoUserInstance = new GeoFire(firebaseDatabase.getReference("geo_user_locations"));
        geoBusInstance = new GeoFire(firebaseDatabase.getReference("geo_bus_locations"));
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public DatabaseReference getRootReference() {
        return firebaseDatabase.getReference();
    }

    public DatabaseReference getUsersReference() {
        return firebaseDatabase.getReference("Users");
    }

    public DatabaseReference getBusReference() {
        return firebaseDatabase.getReference("buses");
    }

    public GeoFire getGeoUserInstance(){
        return geoUserInstance;
    }

    public GeoFire getGeoBusInstance() {
        return geoBusInstance;
    }
}
