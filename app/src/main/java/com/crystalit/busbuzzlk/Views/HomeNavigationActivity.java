package com.crystalit.busbuzzlk.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Const.ServiceParameters;
import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.Fragments.ETAFragment;
import com.crystalit.busbuzzlk.Fragments.HomeOptionsFragment;
import com.crystalit.busbuzzlk.Fragments.OnBusFragment;
import com.crystalit.busbuzzlk.Fragments.WaitingFragment;
import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.Services.BackgroundDetectedActivitiesService;
import com.crystalit.busbuzzlk.ViewModels.HomeNavigationViewModel;
import com.crystalit.busbuzzlk.models.Bus;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        HomeOptionsFragment.OnFragmentInteractionListener, WaitingFragment
                .OnFragmentInteractionListener, OnBusFragment.OnFragmentInteractionListener {

    HomeNavigationViewModel mViewModel;
    FragmentManager fragmentManager;
    LatLng mapLoc;
    float bearing, bearing_accuarcy;
    SupportMapFragment mapFragment;

    boolean autoZoom = true;

    int REQUEST_CHECK_SETTINGS = 1;
    int REQUEST_PERMISSION = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;

    private Location mCurrentLocation;

    private List<String> busKeysFromGeoFire;
    private List<Bus> busList;

    private BroadcastReceiver broadcastReceiver;
    private String TAG = HomeNavigationActivity.class.getSimpleName();

    enum FragmentType {
        HOME_FRAGMENT, WAITING_FRAGMENT, ETA_FRAGMENT, ON_BUS_FRAGMENT
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(ServiceParameters.getBroadcastDetectedActivity()));
    }

    private void requestPermissions() {
        //this will check whether the location settings are satisfied
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.d("tagfordebug", "onSuccess: Location Settings Succeeded");

            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("tagfordebug", "Location Settings failed");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeNavigationActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tagfordebug", "onCreate: started");
        this.mViewModel = ViewModelProviders.of(this).get(HomeNavigationViewModel.class);
        setContentView(R.layout.activity_home_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        busKeysFromGeoFire = new ArrayList<String>();
        busList = new ArrayList<Bus>();


        mapLoc = new LatLng(6.9147, 79.865);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id
                        .map_fragment);
        mapFragment.getMapAsync(this);
        changeFragment(FragmentType.HOME_FRAGMENT);


        createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ServiceParameters.getBroadcastDetectedActivity())) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        startTracking();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            stopTracking();
            mViewModel.logOutUser();
            Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
            startActivity(intent);
            finish();
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.clear();
        MarkerOptions marker = new MarkerOptions().position(mapLoc)
                .title("Your Location");
        int height = 120;
        int width = 120;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_person_standing);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        marker.snippet(Double.toString(mapLoc.latitude) + "," + Double.toString(mapLoc.longitude)
                + ",bearing:" + Float.toString(bearing) + ",bearing_acc:" + Float.toString(bearing_accuarcy));

        googleMap.addMarker(marker);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(mapLoc));
        if(!busList.isEmpty()) {
            for (Bus bus : busList) {
                Log.d("tagfordebug", "onMapReady: marker added");
                MarkerOptions busMarker = new MarkerOptions().position(new LatLng(bus.getLatitude
                        (),bus.getLongitude()))
                        .title(bus.getRouteID());
                int bus_height = 64;
                int bus_width = 64;
                BitmapDrawable bus_bitmapdraw = (BitmapDrawable) getResources().getDrawable(R
                        .drawable.blue_bus);
                Bitmap bus_b = bus_bitmapdraw.getBitmap();
                Bitmap bus_smallMarker = Bitmap.createScaledBitmap(bus_b, bus_width, bus_height,
                        false);
                busMarker.icon(BitmapDescriptorFactory.fromBitmap(bus_smallMarker));

                Marker bus_Marker = googleMap.addMarker(busMarker);
                bus_Marker.showInfoWindow();
            }
        }
        if (autoZoom) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLoc, 16.0f));
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void changeFragment(FragmentType fragmentID) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentID) {
            case HOME_FRAGMENT:
                HomeOptionsFragment fragment = new HomeOptionsFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
                break;
            case WAITING_FRAGMENT:
                WaitingFragment waitingFragment = new WaitingFragment();
                fragmentTransaction.replace(R.id.fragment_container, waitingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case ETA_FRAGMENT:
                ETAFragment etaFragment = new ETAFragment();
                fragmentTransaction.replace(R.id.fragment_container, etaFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case ON_BUS_FRAGMENT:
                OnBusFragment onBusFragment = new OnBusFragment();
                fragmentTransaction.replace(R.id.fragment_container, onBusFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }

    }

    public void showSearchFragment() {
        changeFragment(FragmentType.WAITING_FRAGMENT);
    }

    public void showBusFragment() {
        changeFragment(FragmentType.ON_BUS_FRAGMENT);
    }


    private void updateMap(Location location, boolean changeZoom) {
        this.autoZoom = changeZoom;
        mapLoc = new LatLng(location.getLatitude(), location.getLongitude());
        mapFragment.getMapAsync(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == 0) {
                Toast.makeText(getApplicationContext(), "App would not behave as expected without " +
                        "location services", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    bearing = location.getBearing();

                    // Get the current bus
                    Bus currentBus = UserManager.getInstance().getCurrentBus();
                    // Get old location of the bus
                    if (currentBus!=null) {
                        double oldLocLat = currentBus.getLatitude();
                        double oldLocLong = currentBus.getLongitude();

                        double [] velocity = calculateVelocity(oldLocLat, oldLocLong, location.getLatitude(), location.getLongitude());
                        currentBus.setVelocityX(velocity[0]);
                        currentBus.setVelocityY(velocity[1]);
                    }


                    mViewModel.updateLocationToDatabase(location.getLatitude(),location
                            .getLongitude(),location.getBearing());
                    updateMap(location,true);
                }
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("tagfordebug", "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("tagfordebug", "Location settings are not satisfied. " +
                                        "Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeNavigationActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("tagfordebug", "PendingIntent unable to execute request" +
                                            ".");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("tagfordebug", errorMessage);
                                Toast.makeText(HomeNavigationActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                        }


                    }
                });


    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void getNearestBusesGeoFire(){
        busKeysFromGeoFire.clear();
        Double lat = UserManager.getInstance().getLoggedUser().getLatitude();
        Double lng = UserManager.getInstance().getLoggedUser().getLongitude();
       GeoQuery geoQuery =  Database.getInstance().getGeoBusInstance().queryAtLocation(new
               GeoLocation(lat, lng), 1);
       geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
           @Override
           public void onKeyEntered(String key, GeoLocation location) {
               busKeysFromGeoFire.add(key);
               Log.d("tagfordebug", "onKeyEntered: "+key);

           }

           @Override
           public void onKeyExited(String key) {
               busKeysFromGeoFire.remove(key);
           }

           @Override
           public void onKeyMoved(String key, GeoLocation location) {
               getBusesFromFireBase();
           }

           @Override
           public void onGeoQueryReady() {
                getBusesFromFireBase();

           }

           @Override
           public void onGeoQueryError(DatabaseError error) {

           }
       });


    }

    private void getBusesFromFireBase(){

        for (String key:busKeysFromGeoFire) {
            Log.d("tagfordebug", "getBusesFromFireBase:" + key);
            DatabaseReference ref = Database.getInstance().getBusReference().child(key);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.child("id").getValue().toString();
                        String lat = dataSnapshot.child("latitude").getValue().toString();
                        String lng = dataSnapshot.child("longitude").getValue().toString();
                        String routeId = dataSnapshot.child("routeID").getValue().toString();
                        Bus bus = new Bus(id,Double.parseDouble(lat),Double.parseDouble(lng),
                                routeId);
                    updateBusList(bus);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void updateBusList(Bus bus) {
        String busId = bus.getId();
        for (int i = 0; i < busList.size(); i++) {
            if (busList.get(i).getId().equals(busId)) {
                busList.remove(i);
                busList.add(bus);
                return;
            }
        }
        busList.add(bus);
        return;
    }

    // start/stop tracking user activity recognition
    private void startTracking() {
        Intent intent1 = new Intent(HomeNavigationActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(HomeNavigationActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }

    private void handleUserActivity(int type, int confidence) {
        boolean possibleBus = false;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                possibleBus = true;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                break;
            }
            case DetectedActivity.ON_FOOT: {
                break;
            }
            case DetectedActivity.RUNNING: {
                break;
            }
            case DetectedActivity.STILL: {
                break;
            }
            case DetectedActivity.TILTING: {
                break;
            }
            case DetectedActivity.WALKING: {
                break;
            }
            case DetectedActivity.UNKNOWN: {
                break;
            }
        }

        if (confidence > ServiceParameters.getCONFIDENCE()) {
            //TODO: when changed
            if (possibleBus) {
                if (UserManager.getInstance().getLoggedUser().isInBus()) {
                    // do nothing
                } else {
                    this.showBusFragment();
                }
            } else {
                if (UserManager.getInstance().getLoggedUser().isInBus()) {
                    this.showBusFragment();
                } else {
                    // do nothing
                }
            }
        }
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(ServiceParameters.getBroadcastDetectedActivity()));
    }

    public double[] calculateDistance(double oldLocLat, double oldLocLong, double newLocLat, double newLocLong) {
        // multiply the difference from 111km/degree to get the approximate distance
        double displacementX = (newLocLat - oldLocLat)*111;
        double displacementY = (newLocLong - oldLocLong)*111;

        double [] displacement = {displacementX, displacementY};

        return displacement;
    }

    public double[] calculateVelocity(double oldLocLat, double oldLocLong, double newLocLat, double newLocLong) {
        double [] displacement = calculateDistance(oldLocLat, oldLocLong, newLocLat, newLocLong);
        // calculate velocity at kmph
        double [] velocity = {displacement[0]*60*60/7.5, displacement[1]*60*60/7.5};

        return velocity;
    }

}