package com.example.harry.friendslist.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.example.harry.friendslist.DummyLocationService;
import com.example.harry.friendslist.MainActivity;
import com.example.harry.friendslist.R;
import com.example.harry.friendslist.controller.NotificationReceiver;
import com.example.harry.friendslist.database.DatabaseHandler;
import com.example.harry.friendslist.interfaces.FriendInterface;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by Jay on 3/09/2017.
 */

public class CurrentUser extends Friend implements FriendInterface {

    private String userName;
    private String password;
    private String LOG_TAG = this.getClass().getName();
    private int meetingID = 0;
    private Context context;
    private DatabaseHandler db;
    private int friendsID = 0;

    private List<Friend> friends = new LinkedList<>();
    private List<Meeting> meetings = new LinkedList<>();

    public CurrentUser(String id,String userName, String password, String name, String email, Date dob, Double latitude, Double longitude, Date time, Context context, DatabaseHandler db){
        super(id,name,email,dob,latitude,longitude, time);
        this.userName = userName;
        this.password = password;
        this.context = context;
        this.db = db;
    }

    public void setUsername(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public boolean testPassword(String password){
        if(password.equals(this.password)){
            return true;
        }
        return false;
    }

    public void addAFriend(String name,String email, Date dob){
        Friend newFriend = new Friend(id,name,email,dob);
        if(!friends.contains(newFriend))
            friends.add(newFriend);
        friendsID++;
    }

    public void removeAFriend(String id){
        for(int i = 0; i < friends.size(); i++){
            if(friends.get(i).getId().equals(id)){
                friends.remove(i);
                break;
            }
        }
    }
    public List<Friend> getFriendsList(){
        return friends;
    }
    public List<Meeting> getMeetingList() { return meetings; }

    public void newMeeting(String title, String startTime, String endTime, List<Friend> friends, LatLng latLng){
        Meeting newMeeting = new Meeting(Integer.toString(meetingID), title, startTime, endTime, friends, latLng);
        meetings.add(newMeeting);
        db.addMeeting(newMeeting);
        scheduleNotification(newMeeting);
        meetingID++;
    }

    public void createMeetings(List<Meeting> theMeetings){
        meetings = theMeetings;
    }

    public void createFriends(List<Friend> theFriends){
        friends = theFriends;
    }

    public void removeMeeting(String id){
        for(int i = 0; i < meetings.size(); i++){
            if(meetings.get(i).getID().equals(id)){
                meetings.remove(i);
                break;
            }
        }
    }

    public void loadFromDB(){

        List<Friend> mFriends = new LinkedList<>();
        DummyLocationService dummyLocationService = DummyLocationService.getSingletonInstance(context);

        //Setting time to the latter of the provided times
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 49);
        cal.set(Calendar.SECOND, 0);
        Date date = new Date();
        date = cal.getTime();

        List<DummyLocationService.FriendLocation> matched = dummyLocationService
                .getFriendLocationsForTime(date, 2, 0);

        friends = db.getAllFriends();
        meetings = db.getAllMeetings();
        meetingID = friends.size();
        friendsID = friends.size();

        for(int i = 0; i < matched.size(); i++){
            Friend friend = friends.get(i);
            if(friend.getTime() != matched.get(i).time){
                DummyLocationService.FriendLocation updateFriend = matched.get(i);
                Friend friend1 = new Friend(updateFriend.id, updateFriend.id, updateFriend.latitude, updateFriend.longitude, updateFriend.time);
                db.updateFriend(friend1);
                friends.set(i, friend1);
            }
        }
    }

    public void loadFirstTime(Context context){


        String name, mTitle, mStartTime, mEndTime, mFriendID;
        int noFriends;
        Double lat, lng;
        LatLng mLatLng;

        List<Friend> mFriends = new LinkedList<>();
        DummyLocationService dummyLocationService = DummyLocationService.getSingletonInstance(context);

        //Setting time to the latter of the provided times
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 49);
        cal.set(Calendar.SECOND, 0);
        Date date = new Date();
        date = cal.getTime();

        List<DummyLocationService.FriendLocation> matched = dummyLocationService
                .getFriendLocationsForTime(date, 2, 0);

        for(int i = 0; i < matched.size(); i++){
            DummyLocationService.FriendLocation newFriend = matched.get(i);
            name = newFriend.name;
            //Remove the digit in the name
            name = name.replaceAll("\\d","");

            Friend friend = new Friend(newFriend.id, name, newFriend.latitude, newFriend.longitude, newFriend.time);
            friends.add(friend);
            db.addFriend(friend);
            Log.i(LOG_TAG, "ID : " + friend.id);
            Log.i(LOG_TAG, "LAT : " + friend.latitude);
        }

        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.meetings_file)))
        {
            // match comma and 0 or more whitepace (to catch newlines)
            scanner.useDelimiter(",\\s*");
            while (scanner.hasNext())
            {
                mTitle = scanner.next();
                mStartTime = scanner.next();
                mEndTime = scanner.next();
                noFriends = Integer.parseInt(scanner.next());
                for(int i = 0; i < noFriends; i++){
                    mFriendID = scanner.next();
                    for(int j = 0; j < friends.size(); j++){
                        if(mFriendID == friends.get(j).getId()){
                            mFriends.add(friends.get(j));
                        }
                    }
                }
                lat = Double.parseDouble(scanner.next());
                lng = Double.parseDouble(scanner.next());
                mLatLng = new LatLng(lat, lng);

                newMeeting(mTitle, mStartTime, mEndTime, mFriends, mLatLng);
            }
        } catch (Resources.NotFoundException e)
        {
            Log.i(LOG_TAG, "File Not Found Exception Caught");
        }
    }

    private void scheduleNotification(Meeting meeting){
        String meetingTime = meeting.getStartTime();
        Long lMeetingTime, currentTime, alarmTime;

        //Getting time of day in miliseconds
        StringTokenizer st = new StringTokenizer(meetingTime, ":");
        Log.i(LOG_TAG, "Meeting time" + meetingTime);
        lMeetingTime = Long.parseLong(st.nextToken()) * 3600000;
        lMeetingTime = lMeetingTime + (Long.parseLong(st.nextToken()) * 60000);
        Log.i(LOG_TAG, "Meeting time" + lMeetingTime);

        //Getting current time since midnight in miliseconds
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        currentTime = now - c.getTimeInMillis();

        alarmTime = lMeetingTime - currentTime;

        //If meeting time has passed for the day its for tomorrow e.g meeting time is 3PM system time is 4PM
        if(alarmTime < 0){
            alarmTime = alarmTime + 24*60*60*1000;
        }

        if(alarmTime > 600000){
            alarmTime = alarmTime - 600000;
        }
        Log.i(LOG_TAG, "meeting time: " +lMeetingTime + " ... current time: " + currentTime + " .... alarm time: " + alarmTime);

        Intent intentAlarm = new Intent(context, NotificationReceiver.class);
        intentAlarm.putExtra("name", meeting.getTitle());
        intentAlarm.putExtra("start", meeting.getStartTime());
        intentAlarm.putExtra("end", meeting.getEndTime());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+alarmTime, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }
}
