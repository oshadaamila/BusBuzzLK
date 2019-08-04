package com.crystalit.busbuzzlk.Database.Dao;

import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.models.Bus;
import com.crystalit.busbuzzlk.models.User;
import com.firebase.geofire.GeoLocation;

public class BusDao {

    public BusDao() {
    }

    public void addNewBusToDatabase(Bus bus) {
        //set the values in
        Database database = Database.getInstance();
        database.getBusReference().child(bus.getId()).setValue(bus);
        //add the user to bus
        User currentUser = UserManager.getInstance().getLoggedUser();
        database.getBusReference().child(bus.getId()).child("travellers").child(currentUser.getuName())
                .child("bearing").setValue(currentUser.getBearing());
        //set geohash of the bus
        database.getGeoBusInstance().setLocation(bus.getId(),new GeoLocation(bus.getLatitude(),
                bus.getLongitude()));
    }

    public void updateBusLocation(Bus bus,Double latitude,Double longitude,Double bearing){

        String userName = UserManager
                .getInstance().getLoggedUser().getuName();
        //update the bus locations
        Database database = Database.getInstance();
        database.getBusReference().child(bus.getId()).child("latitude").setValue(latitude);
        database.getBusReference().child(bus.getId()).child("longitude").setValue(longitude);
        database.getBusReference().child(bus.getId()).child("travellers").child(userName).child
                ("bearing").setValue(bearing);
        //update the geofire bus locations
        database.getGeoBusInstance().setLocation(bus.getId(), new GeoLocation(latitude,
                longitude));
    }

}
