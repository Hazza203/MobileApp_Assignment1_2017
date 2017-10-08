package com.example.harry.friendslist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.harry.friendslist.controller.AsyncDistanceDemand;
import com.example.harry.friendslist.controller.NotificationReceiver;
import com.example.harry.friendslist.controller.SuggestTimerTask;
import com.example.harry.friendslist.model.CurrentUser;
import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Meeting;
import com.example.harry.friendslist.model.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import static java.util.Calendar.AM;

/*Main Activity: Begins the application, requests permission, sets up the map, the
    navigation bar and switches to the other fragments. The Map is the main implementation
    of this activity as it can make meetings on map click, display friends location aswell as
    users own location.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private String LOG_TAG = this.getClass().getName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private final int MY_PERMISSIONS_FINE_LOCATION = 2;
    private List<Friend> friendsList;
    private List<Meeting> meetingsList;
    private boolean isMainShown = true;
    private ProgressDialog progressDialog;

    private Model model;
    private CurrentUser user;
    public static boolean alive;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alive = true;
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = Model.getInstance();

        //Login dummy user and create model
        try {
            Date time = DateFormat.getTimeInstance(DateFormat.MEDIUM).parse("12:00:00 PM");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dob = dateFormat.parse("01/01/1970");
            model.setCurrentUserString("1", "userName", "Pass1234", "Test User", "test@test", dob, time, MainActivity.this);
            user = model.getCurrentUser();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessFineLocation()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_LOCATION);
            } else {
                //Check location services are turned on and updates with users current location sends alert if not.
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                if (!isLocationEnabled(locationManager)) {
                    showLocationAlert();
                } else {
                    locationUpdater();
                }
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
            if (!canReadContacts()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_READ_CONTACTS);
            }
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

        Button suggestMeetingButton = (Button)findViewById(R.id.suggestMeetingButton);
        suggestMeetingButton.setOnClickListener(this);

        periodicallySuggest();

    }

    //Handles back button being pressed as on a fragment it would exit the app
    // overriden to take the user back to the home page if on a fragment
    @Override
    public final void onBackPressed()
    {
        if (isMainShown)
        {
            // We're in the MAIN Fragment.
            finish();
        }
        else
        {
            // We're somewhere else, reload the MAIN Fragment.
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
        }
    }

    //Check for fine location permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canAccessFineLocation() {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    //Check for readcontacts permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canReadContacts() {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_CONTACTS));
    }

    //Overriden method which handles permission results
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
                    //Check location services are turned on and updates with users current location sends alert if not.
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    if (!isLocationEnabled(locationManager)) {
                        showLocationAlert();
                    } else {
                        locationUpdater();
                    }
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                } else {

                }
                return;
            }
        }
    }

    //Checks if location services are turned on
    public boolean isLocationEnabled(LocationManager locationManager) {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Gets users current location continuously
    private void locationUpdater(){
        PackageManager pm = this.getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                this.getPackageName());
        Log.i(LOG_TAG, "Perms" + hasPerm + " " + PackageManager.PERMISSION_GRANTED);
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            locationListener = new LocationListener() {
                @Override
            public void onLocationChanged(Location location) {
                user.setLatitude(location.getLatitude());
                user.setLongitude(location.getLongitude());
                Log.i(LOG_TAG, "Location changed");
            }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            };
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    //Displays alerts asking to turn on location services
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

    //Toggles nav bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //If navigation item clicked, loads intended fragment
    public void navigationItemClicked() {


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment fragment = null;
                String tag = "";
                switch (menuItem.getItemId()) {
                    //As home is the activity and not a fragment, remove all fragments from view.
                    case (R.id.home):
                        isMainShown = true;
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
                        isMainShown = false;
                        fragment = new addfriend_Fragment();
                        tag = "addFriendFragTag";
                        break;
                    case (R.id.view_friends):
                        isMainShown = false;
                        fragment = new listFriends();
                        tag = "viewFriendFragTag";
                        break;
                    case (R.id.view_meetings):
                        isMainShown = false;
                        fragment = new ViewMeetings_Fragment();
                        tag = "viewMeetingsFragTag";
                        break;
                    case (R.id.how_to):
                        isMainShown = false;
                        fragment = new howTo_Fragment();
                        tag = "howToFragTag";
                        break;
                }

                if(fragment != null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.map, fragment, tag);
                    ft.commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    //Gets google map ready for display, loads markers with info
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        onMapClick();
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        LatLng myLocation = new LatLng(-37.8136, 144.9634);
        //Move and zoom camera to melbourne
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        setMarkers(mMap);
        mMap.setMyLocationEnabled(true);

        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
                @Override
                public View getInfoWindow(Marker marker){
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    String friendStr = null;
                    Date time = null;
                    View view = getLayoutInflater().inflate(R.layout.infowindow, null);

                    TextView tvName = view.findViewById(R.id.friend_name);
                    TextView tvTime = view.findViewById(R.id.time);
                    final LatLng latLng = marker.getPosition();

                    double lat = latLng.latitude;
                    double lng = latLng.longitude;

                    friendsList = user.getFriendsList();
                    meetingsList = user.getMeetingList();

                    for(int i = 0; i < friendsList.size(); i++){
                        Friend friend = friendsList.get(i);
                        if(friend.getLatitude() == lat && friend.getLongitude() == lng){
                            friendStr = friend.getName();
                            time = friend.getTime();
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss aa");
                            tvName.setText("Name: " + friendStr);
                            if(time != null){
                                tvTime.setText("Time: " + dateFormat.format(time));
                            }
                            return view;
                        }
                    }
                    for(int i = 0; i < meetingsList.size(); i++){
                        Meeting meeting = meetingsList.get(i);
                        if(meeting.getLocation() != null){
                            if((meeting.getLocation().latitude == lat) && (meeting.getLocation().longitude == lng)){
                                tvName.setText(meeting.getTitle());
                                if(meeting.getStartTime() != null){
                                    tvTime.setText(meeting.getStartTime() + " - " + meeting.getEndTime());
                                }else{
                                    tvTime.setText("Unspecified Time");
                                }
                                return view;
                            }
                        }
                    }

                    return view;
                }
            });
        }
    }

    //Listens fr mapClicks, on click creates dialog for a new meeting
    public void onMapClick(){
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                makeMeeting(latLng);
            }
        });
    }

    //Sets the markers of friends on the map
    private void setMarkers(GoogleMap googleMap) {
        Log.i(LOG_TAG, "In Set markers");
        int size, count = 0;
        double lat, lng;

        mMap = googleMap;
        friendsList = user.getFriendsList();
        meetingsList = user.getMeetingList();

        while (count < friendsList.size()) {
            Friend friend = friendsList.get(count);

            lat = friend.getLatitude();
            lng = friend.getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            count++;
        }
        count = 0;
        while(count < meetingsList.size()){
            Meeting meeting = meetingsList.get(count);
            if(meeting.getLocation() != null){
                mMap.addMarker(new MarkerOptions()
                .position(meeting.getLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            }
            count++;
        }
    }

    //Creates alertdialog with input for details of the meeting and then sends creates a new meeting
    //Code gets a little confusing with all the dialog boxes but works efficiently
    private void makeMeeting(final LatLng latLng){
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;
        Fragment fragment = new addMeeting_Fragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, fragment);
        ft.commit();
    }

    @Override
    public void onClick(View v){
        Button button = (Button) findViewById(R.id.suggestMeetingButton);
        if(button.getText().equals("Click to turn on auto suggestions")){
            if(isNetworkAvailable()){
                button.setText("Suggest Now!");
                periodicallySuggest();
                return;
            }
            else{
                Toast.makeText(this, "Network still unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        switch(v.getId()){
            case    R.id.suggestMeetingButton:{
                if(isNetworkAvailable()){
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Finding closest friend...");
                    progressDialog.show();
                    AsyncDistanceDemand asyncDistanceDemand = new AsyncDistanceDemand(this, null);
                    asyncDistanceDemand.execute();
                } else{
                    Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void periodicallySuggest(){
        if(isNetworkAvailable()){
            Timer timer = new Timer();
            timer.schedule(new SuggestTimerTask(this), 20000);
            timer.scheduleAtFixedRate(new SuggestTimerTask(this), 290000, 290000);
        }
        else {
            Button button = (Button) findViewById(R.id.suggestMeetingButton);
            button.setText("Click to turn on auto suggestions");
        }
    }

    public void failedDistance(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        Toast.makeText(MainActivity.this, "Please wait a while.", Toast.LENGTH_LONG).show();
    }

    public void onDistanceCalculated(String duration, Double distance, Friend friend, final String[] exclFriends, String location, final LatLng latLng){

        final String friendID = friend.getId();
        final String[] exclIDs = exclFriends;

        if(progressDialog != null){
            progressDialog.dismiss();
        }

        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle("Schedule meeting?");
        alert.setMessage("Your closest friend is " + friend.getName() + "! Would you like to meet him at " + location + "?\n" +
        "Distance: " + distance.toString() + " km's\nWalking time: " + duration);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Fragment fragment = new addMeeting_Fragment();
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", latLng.latitude);
                bundle.putDouble("lng", latLng.longitude);
                bundle.putString("friend", friendID);
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.map, fragment);
                ft.commit();
            }
        });

        alert.setNeutralButton("Next suggestion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] excl = new String[exclFriends.length + 1];
                int count = 0;
                for(int j = 0; j < exclFriends.length; j++){
                    excl[j] = exclFriends[j];
                    count++;
                }
                excl[count] = friendID;
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Finding next closest friend...");
                progressDialog.show();
                AsyncDistanceDemand asyncDistanceDemand = new AsyncDistanceDemand(MainActivity.this, excl);
                asyncDistanceDemand.execute();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        alive = false;
        Log.i(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        alive = false;
        Log.i(LOG_TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        alive = false;
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

}
