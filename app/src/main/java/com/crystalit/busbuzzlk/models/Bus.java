package com.crystalit.busbuzzlk.models;

public class Bus {

    String id;
    Double latitude,longitude;
    String routeID;
    Double bearing = 0.0;
    private Double velocityX;
    private Double velocityY;

    public Bus(String id, Double latitude, Double longitude, String routeID) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeID = routeID;
        this.setVelocityX(0.0);
        this.setVelocityY(0.0);
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


    public Double getVelocityX() {
        return velocityX;
    }

    public Double getVelocityY() {
        return velocityY;
    }

    public void setVelocityX(Double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(Double velocityY) {
        this.velocityY = velocityY;
    }
}
