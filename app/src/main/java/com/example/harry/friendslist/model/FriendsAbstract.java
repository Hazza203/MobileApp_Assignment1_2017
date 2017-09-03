package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.FriendInterface;

import java.util.Date;

/**
 * Created by Jay on 1/09/2017.
 */

public abstract class FriendsAbstract implements FriendInterface{

    String id;
    String name;
    String email;
    Date dob;
    double latitude;
    double longitude;
    Date time;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getBirthday() {
        return dob;
    }
    public void setBirthday(Date dob) {
        this.dob = dob;
    }
    public Double getLongitude(){
        return longitude;
    }
    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }
    public Double getLatitude(){
        return latitude;
    }
    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
}
