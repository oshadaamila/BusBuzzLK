package com.crystalit.busbuzzlk.models;

public class User {

    private String uName, password, email;

    public User(String uName, String password, String email) {
        this.uName = uName;
        this.password = password;
        this.email = email;
    }

    public String getuName() {
        return uName;
    }
}
