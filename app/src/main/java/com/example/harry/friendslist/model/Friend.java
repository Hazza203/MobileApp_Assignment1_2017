package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.FriendInterface;

import java.util.Date;

/**
 * Created by Jay on 1/09/2017.
 */

public class Friend extends FriendsAbstract implements FriendInterface{

    public Friend(String id, String name,String email, Date dob){
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.latitude = Double.NaN;
        this.longitude = Double.NaN;
    }

    public Friend(String id, String name,String email, Date dob, Double latitude, Double longitude, Date time){
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
