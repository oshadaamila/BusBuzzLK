package com.crystalit.busbuzzlk.models;

public class User {

    private String uName, password, email;

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
}
