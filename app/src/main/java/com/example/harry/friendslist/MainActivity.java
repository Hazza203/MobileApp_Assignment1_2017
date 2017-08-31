package com.example.harry.friendslist;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String LOG_TAG = this.getClass().getName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private LocationManager locationManager;
    private final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private final int MY_PERMISSIONS_FINE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain and draw the navigation bar

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){

        }

        nv = (NavigationView) findViewById(R.id.nav1);
        nv.bringToFront();
        navigationItemClicked();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get Permissions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessFineLocation()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_READ_CONTACTS);
            }
            if (!canReadContacts()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_FINE_LOCATION);
            }
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessFineLocation() {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canReadContacts() {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_CONTACTS));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case MY_PERMISSIONS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                    if (!isLocationEnabled(locationManager)) {
                        showLocationAlert();
                    } else {
                    }

                } else {

                }
                return;
            }
        }
    }

    public boolean isLocationEnabled(LocationManager locationManager) {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showLocationAlert(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location").setMessage("Your Location Services is Disabled. \nPlease Enable Location.")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface paramDialoginterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt){
                    }
                });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigationItemClicked() {


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case (R.id.add_friend):
                        Log.i(LOG_TAG, "Add friend clicked");
                        fragment = new addfriend_Fragment();
                        break;
                    case (R.id.settings):
                        fragment = new settings_Fragment();
                        break;
                    case (R.id.schedule_meeting):
                        fragment = new scheduleMeeting_Fragment();
                        break;
                }

                if(fragment != null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.nav1, fragment);
                    ft.commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        LatLng myLocation = new LatLng(-37.8136, 144.9634);
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        setMarkers(mMap);
    }

    private void setMarkers(GoogleMap googleMap) {
        Log.i(LOG_TAG, "In Set markers");
        int size, count = 0;
        double lat, lng;

        mMap = googleMap;
        DummyLocationService dummyLocationService = DummyLocationService.getSingletonInstance(MainActivity.this);
        try {
            Date time = DateFormat.getTimeInstance(DateFormat.MEDIUM).parse("12:00:00 PM");
            List<DummyLocationService.FriendLocation> friendLocations = dummyLocationService.getFriendLocationsForTime(time, 719, 0);
            dummyLocationService.logAll();
            size = friendLocations.size();

            Log.i(LOG_TAG, "Size: " + size);

            while (count < size) {
                DummyLocationService.FriendLocation friendLocation = friendLocations.get(count);
                lat = friendLocation.latitude;
                lng = friendLocation.longitude;
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                count++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onRestart()
    {
        Log.i(LOG_TAG, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        Log.i(LOG_TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        Log.i(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.i(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Log.i(LOG_TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

}
