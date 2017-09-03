package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.FriendInterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jay on 1/09/2017.
 */

public class Friend extends FriendsAbstract implements FriendInterface{

    public Friend(String name,String email) {
        this.id = "001";
        this.name = name;
        this.email = email;
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.dob = dateformat.parse("01/01/2000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.latitude = Double.NaN;
        this.longitude = Double.NaN;
    }

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
