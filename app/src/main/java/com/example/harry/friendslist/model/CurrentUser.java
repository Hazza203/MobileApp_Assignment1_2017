package com.example.harry.friendslist.model;

import android.support.v4.app.Fragment;

import com.example.harry.friendslist.interfaces.FriendInterface;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jay on 3/09/2017.
 */

public class CurrentUser extends Friend implements FriendInterface {

    private String userName;
    private String password;

    private List<Friend> friends = new LinkedList<>();
    private List<Meeting> meetings = new LinkedList<>();

    public CurrentUser(String id,String userName, String password, String name, String email, Date dob, Double latitude, Double longitude){
        super(id,name,email,dob,latitude,longitude);
        this.userName = userName;
        this.password = password;
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
}
