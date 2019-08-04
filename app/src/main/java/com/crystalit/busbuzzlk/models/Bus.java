package com.crystalit.busbuzzlk.models;

public class Bus {

    String id;
    Double latitude,longitude;
    String routeID;
    Double bearing = 0.0;

    public Bus(String id, Double latitude, Double longitude, String routeID) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeID = routeID;
    }

    public Bus(String id,Double latitude,Double longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getRouteID() {
        return routeID;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }
}
