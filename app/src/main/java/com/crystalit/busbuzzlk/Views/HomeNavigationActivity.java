package com.crystalit.busbuzzlk.Views;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
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

import com.crystalit.busbuzzlk.Fragments.ETAFragment;
import com.crystalit.busbuzzlk.Fragments.HomeOptionsFragment;
import com.crystalit.busbuzzlk.Fragments.WaitingFragment;
import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.ViewModels.HomeNavigationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.widget.Toast.LENGTH_LONG;

public class HomeNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        HomeOptionsFragment.OnFragmentInteractionListener, WaitingFragment.OnFragmentInteractionListener {

    HomeNavigationViewModel mViewModel;
    FragmentManager fragmentManager;
    LatLng mapLoc;
    SupportMapFragment mapFragment;

    private FusedLocationProviderClient fusedLocationClient;

    enum FragmentType {
        HOME_FRAGMENT, WAITING_FRAGMENT, ETA_FRAGMENT
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        }else{
            Log.d("Loc", "permission check granted");
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new
                    OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("Loc", "onSuccess: Location retrieving success");
                    updateMap(location);
                }else{
                    Toast.makeText(getApplicationContext(),"Turn on location services and internet services",
                            LENGTH_LONG).show();
                }
            }
        });
        }


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

        MarkerOptions marker = new MarkerOptions().position(mapLoc)
                .title("Your Location");
        int height = 120;
        int width = 120;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_action_person_standing);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        googleMap.addMarker(marker);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(mapLoc));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLoc, 13.0f));

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

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
        }

    }

    public void showSearchFragment(){
        changeFragment(FragmentType.WAITING_FRAGMENT);
    }

    private void updateMap(Location location){
        mapLoc = new LatLng(location.getLatitude(),location.getLongitude());
        mapFragment.getMapAsync(this);
    }


}
