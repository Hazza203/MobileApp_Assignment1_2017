package com.example.harry.friendslist;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = this.getClass().getName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nav1);
        navigationItemClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigationItemClicked(){

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.add_friend):
                        Intent add_friend = new Intent(getApplicationContext(), AddFriend_Activity.class);
                        startActivity(add_friend);
                    case (R.id.settings):
                        Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settings);
                    case (R.id.schedule_meeting):
                        Intent schedule_meetings = new Intent(getApplicationContext(), ScheduleMeetingActivity.class);
                        startActivity(schedule_meetings);
                }
                return true;
            }
        });
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
