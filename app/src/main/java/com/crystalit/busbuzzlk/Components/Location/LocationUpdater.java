package com.crystalit.busbuzzlk.Components.Location;

import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class LocationUpdater {

    LocationRequest locationRequest;
    Context context;
    public LocationUpdater(Context context){
        this.context = context;
    }

    public Task checkLocationSettings(){
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public LocationRequest getLocationRequest() {
        if(locationRequest == null){
            createLocationRequest();
        }
        return locationRequest;
    }


}
