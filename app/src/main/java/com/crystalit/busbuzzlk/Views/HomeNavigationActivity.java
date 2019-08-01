package com.crystalit.busbuzzlk.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crystalit.busbuzzlk.Components.Location.LocationUpdater;
import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Fragments.ETAFragment;
import com.crystalit.busbuzzlk.Fragments.HomeOptionsFragment;
import com.crystalit.busbuzzlk.Fragments.OnBusFragment;
import com.crystalit.busbuzzlk.Fragments.WaitingFragment;
import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.ViewModels.HomeNavigationViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.widget.Toast.LENGTH_LONG;

public class HomeNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        HomeOptionsFragment.OnFragmentInteractionListener, WaitingFragment
                .OnFragmentInteractionListener,OnBusFragment.OnFragmentInteractionListener {

    HomeNavigationViewModel mViewModel;
    FragmentManager fragmentManager;
    LatLng mapLoc;
    float bearing, bearing_accuarcy;
    SupportMapFragment mapFragment;
    LocationUpdater locationUpdater;
    boolean autoZoom = true;

    int REQUEST_CHECK_SETTINGS = 1;
    int REQUEST_PERMISSION =2 ;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    enum FragmentType {
        HOME_FRAGMENT, WAITING_FRAGMENT, ETA_FRAGMENT,ON_BUS_FRAGMENT
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }else {
            fusedLocationClient.requestLocationUpdates(locationUpdater.getLocationRequest(),
                    locationCallback,
                    null /* Looper */);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(HomeNavigationViewModel.class);
        setContentView(R.layout.activity_home_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();


        mapLoc = new LatLng(6.9147, 79.865);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id
                        .map_fragment);
        mapFragment.getMapAsync(this);
        changeFragment(FragmentType.HOME_FRAGMENT);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationUpdater =new LocationUpdater(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);

        }else{
            Log.d("Loc", "permission check granted");
            checkLocationSettings();
        }
        locationCallback = new LocationCallback(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    bearing = location.getBearing();
                    bearing_accuarcy = location.getBearingAccuracyDegrees();
                    mViewModel.updateLocationToDatabase(location.getLatitude(),location
                            .getLongitude(),location.getBearing());
                    updateMap(location,false);
                }
            };

        };



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_logout ){
            mViewModel.logOutUser();
            Intent intent = new Intent(getApplicationContext(),LauncherActivity.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_action_person_standing);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        marker.snippet(Double.toString(mapLoc.latitude)+","+Double.toString(mapLoc.longitude)
                +",bearing:"+Float.toString(bearing)+",bearing_acc:"+Float.toString(bearing_accuarcy));

        googleMap.addMarker(marker);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(mapLoc));
        if(autoZoom){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLoc, 16.0f));
        }

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void checkLocationSettings(){
        Task checkSettingsTask = locationUpdater.checkLocationSettings();
        checkSettingsTask.addOnSuccessListener(this, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d("LocationSettings", "onSuccess: Location Settings task succeeded");
                updateLastKnownLocation();
            }
        });
        checkSettingsTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LocationSettings", "onSuccess: Location Settings task failed");
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



    private void changeFragment(FragmentType fragmentID){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch(fragmentID){
            case HOME_FRAGMENT:
                HomeOptionsFragment fragment = new HomeOptionsFragment();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
                break;
            case WAITING_FRAGMENT:
                WaitingFragment waitingFragment = new WaitingFragment();
                fragmentTransaction.replace(R.id.fragment_container,waitingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case ETA_FRAGMENT:
                ETAFragment etaFragment = new ETAFragment();
                fragmentTransaction.replace(R.id.fragment_container,etaFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case ON_BUS_FRAGMENT:
                OnBusFragment onBusFragment = new OnBusFragment();
                fragmentTransaction.replace(R.id.fragment_container,onBusFragment);
                fragmentTransaction.commit();
        }

    }

    public void showSearchFragment(){
        changeFragment(FragmentType.WAITING_FRAGMENT);
    }

    public void showBusFragment(){
        changeFragment(FragmentType.ON_BUS_FRAGMENT);
    }


    private void updateMap(Location location,boolean changeZoom){
        this.autoZoom = changeZoom;
        mapLoc = new LatLng(location.getLatitude(),location.getLongitude());
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("MissingPermission")
    private void updateLastKnownLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new
                OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("Loc", "onSuccess: Location retrieving success");

                    updateMap(location,true);
                }else{
                    Toast.makeText(getApplicationContext(),"Turn on location services and internet services",
                            LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_CHECK_SETTINGS){
            if (resultCode == 0){
                Toast.makeText(getApplicationContext(),"App would not behave as expected without " +
                        "location services",Toast.LENGTH_LONG).show();
            }
        }
    }
}
