package com.example.harry.friendslist.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.harry.friendslist.DummyLocationService;
import com.example.harry.friendslist.MainActivity;
import com.example.harry.friendslist.R;
import com.example.harry.friendslist.interfaces.FriendInterface;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Jay on 3/09/2017.
 */

public class CurrentUser extends Friend implements FriendInterface {

    private String userName;
    private String password;
    private String LOG_TAG = this.getClass().getName();

    private List<Friend> friends = new LinkedList<>();
    private List<Meeting> meetings = new LinkedList<>();

    public CurrentUser(String id,String userName, String password, String name, String email, Date dob, Double latitude, Double longitude, Context context){
        super(id,name,email,dob,latitude,longitude);
        this.userName = userName;
        this.password = password;
        loadData(context);
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
    public void addAFriend(String id, String name,String email, Date dob){
        Friend newFriend = new Friend(id,name,email,dob);
        if(!friends.contains(newFriend))
            friends.add(newFriend);
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

    public void newMeeting(String id, String title, String startTime, String endTime, List<Friend> friends, LatLng latLng){
        Meeting newMeeting = new Meeting(id, title, startTime, endTime, friends, latLng);
        meetings.add(newMeeting);
    }

    public void removeMeeting(String id){
        for(int i = 0; i < meetings.size(); i++){
            if(meetings.get(i).getID().equals(id)){
                meetings.remove(i);
                break;
            }
        }
    }

    private void loadData(Context context){

        //Need to replace dummy_data.txt with actual friend and meeting files
        String fID, fName, fEmail, mID, mTitle, mStartTime, mEndTime, mFriendID;
        int noFriends;
        Date fDOB;
        Double lat, lng, fLat, fLng;
        LatLng mLatLng;
        List<Friend> mFriends = new LinkedList<>();
        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.dummy_data)))
        {
            // match comma and 0 or more whitepace (to catch newlines)
            scanner.useDelimiter(",\\s*");
            while (scanner.hasNext())
            {
                fID = scanner.next();
                fName = scanner.next();
                fEmail = scanner.next();
                fDOB = DateFormat.getDateInstance(DateFormat.MEDIUM).parse(scanner.next());
                fLat = Double.parseDouble(scanner.next());
                fLng = Double.parseDouble(scanner.next());
                Friend friend = new Friend(fID, fName, fEmail, fDOB, fLat, fLng);
                friends.add(friend);
            }
        } catch (Resources.NotFoundException e)
        {
            Log.i(LOG_TAG, "File Not Found Exception Caught");
        } catch (ParseException e)
        {
            Log.i(LOG_TAG, "ParseException Caught (Incorrect File Format)");
        }

        try (Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.dummy_data)))
        {
            // match comma and 0 or more whitepace (to catch newlines)
            scanner.useDelimiter(",\\s*");
            while (scanner.hasNext())
            {
                mID = scanner.next();
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
                Meeting meeting = new Meeting(mID, mTitle, mStartTime, mEndTime, mFriends, mLatLng);
                meetings.add(meeting);
            }
        } catch (Resources.NotFoundException e)
        {
            Log.i(LOG_TAG, "File Not Found Exception Caught");
        }
    }
}
