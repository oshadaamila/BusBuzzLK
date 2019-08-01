package com.crystalit.busbuzzlk.models;

public class User {

    private String uName, password, email,routeNo;
    private double latitude=0.0;
    private double longitude=0.0;
    private boolean inBus=false;



    public User(String uName, String email, String password) {
        this.uName = uName;
        this.password = password;
        this.email = email;
    }

    public User() {
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getuName() {
        return uName;
    }

    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isInBus() {
        return inBus;
    }

    public void setInBus(boolean inBus) {
        this.inBus = inBus;
    }

    public String getRouteNo() {
        if (routeNo.isEmpty()){
            return "0";
        }
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
