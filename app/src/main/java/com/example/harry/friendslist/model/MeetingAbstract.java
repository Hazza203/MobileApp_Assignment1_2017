package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.MeetingInterface;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by harry on 3/09/2017.
 */

public  class MeetingAbstract implements MeetingInterface{

    String id;
    String title;
    String startTime;
    String endTIme;
    LatLng latLng;
    List<Friend> friends;

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTIme;
    }
    public void setEndTime(String endTIme) {
        this.endTIme = endTIme;
    }
    public LatLng getLocation(){return latLng;}
    public void setLocation(LatLng latLng) {this.latLng = latLng;}
    public void addFriend(Friend friend) { }
    public void removeFriend(Friend friend) {

    }
    public List<Friend> getInvited() {
        return friends;
    }

}
