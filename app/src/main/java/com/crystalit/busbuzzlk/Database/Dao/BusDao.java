package com.crystalit.busbuzzlk.Database.Dao;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.models.Bus;

public class BusDao {
    public BusDao() {
    }

    public void addNewBusToDatabase(Bus bus) {
        Database database = Database.getInstance();
        database.getBusReference().child(bus.getId()).setValue(bus);

    }

}
