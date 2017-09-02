package com.example.harry.friendslist;

import android.Manifest;
import android.app.TimePickerDialog;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(locationManager)) {
            showLocationAlert();
        }

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
                Log.i(LOG_TAG, "Granting permision");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    public boolean isLocationEnabled(LocationManager locationManager) {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
                    case (R.id.home):
                        int i = 0;
                        FragmentManager fm = getSupportFragmentManager();
                        for(Fragment frag:fm.getFragments()){
                            if(i == 0){
                                i++;
                                continue;
                            }
                            if(frag != null) {
                                Log.i(LOG_TAG, "Removed frag");
                                getSupportFragmentManager().beginTransaction().remove(frag).commit();
                            }
                        }
                        getSupportActionBar().setTitle("Friends Locations");
                        break;
                    case (R.id.add_friend):
                        Log.i(LOG_TAG, "Add friend clicked");
                        fragment = new addfriend_Fragment();
                        break;
                    case (R.id.settings):
                        fragment = new settings_Fragment();
                        break;
                    case (R.id.how_to):
                        fragment = new howTo_Fragment();
                        break;
                }

                if(fragment != null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.map, fragment);
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
        onMapClick();
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

        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
                @Override
                public View getInfoWindow(Marker marker){
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    final String friend = null;
                    View view = getLayoutInflater().inflate(R.layout.infowindow, null);

                    TextView name = view.findViewById(R.id.friend_name);
                    TextView time = view.findViewById(R.id.time);
                    final Button makeMeeting = view.findViewById(R.id.makeMeeting);
                    final LatLng latLng = marker.getPosition();

                    name.setText(R.string.name);
                    time.setText(R.string.updateTime);
                    makeMeeting.setText(R.string.meeting);

                    makeMeeting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            makeMeeting(latLng, friend);
                        }
                    });
                    return view;
                }
            });
        }
    }

    public void onMapClick(){
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                makeMeeting(latLng, null);
            }
        });
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

    private void makeMeeting(LatLng latLng, String name){
        boolean addMore = true;
        int startHour =24, startMin = 24;

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle("Create new meeting");

        final EditText title = new EditText(MainActivity.this);
        title.setHint("Title");
        layout.addView(title);

        final EditText startTime = new EditText(MainActivity.this);
        startTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        startTime.setHint("Start Time");
        startTime.setKeyListener(null);
        layout.addView(startTime);

        final EditText endTime = new EditText(MainActivity.this);
        endTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String sTime = startTime.getText().toString();
                        String time[] = sTime.split(":");

                        int hour = Integer.parseInt(time[0]);
                        int min = Integer.parseInt(time[1]);

                        if(hour == selectedHour){
                            if(selectedMinute > min){
                                endTime.setText( selectedHour + ":" + selectedMinute);
                            }
                        }else if(selectedHour > hour){
                            endTime.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        endTime.setHint("End Time");
        endTime.setKeyListener(null);
        layout.addView(endTime);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String mName = title.getText().toString();
                String sTime = startTime.getText().toString();
                String eTime = endTime.getText().toString();
                if(mName.equals("")|| sTime.equals("") || eTime.equals("")){
                    return;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                final ArrayList selectedItems = new ArrayList();
                final CharSequence[] friends = {"Place","Holders"};
                alert.setTitle("Select Friends");
                alert.setMultiChoiceItems(friends, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            // indexSelected contains the index of item (of which checkbox checked)
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    // write your code when user checked the checkbox
                                    selectedItems.add(indexSelected);
                                } else if (selectedItems.contains(indexSelected)) {
                                    // Else, if the item is already in the array, remove it
                                    // write your code when user Uchecked the checkbox
                                    selectedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        });
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

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
