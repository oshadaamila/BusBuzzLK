package com.crystalit.busbuzzlk.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private static Database instance;

    private FirebaseDatabase firebaseDatabase;

    private Database() {
        firebaseDatabase = FirebaseDatabase.getInstance();
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



}
