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

    int idIncrement = 101;

    public Friend(String name,String email) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = dateformat.parse("01/01/1970");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.name = name;
        this.id = Integer.toString(idIncrement);
        this.email = email;
        this.dob = date;
    }

    //Pass retrieved dummyLocationService friend instance to here
    public Friend(String id, String name, Double lat, Double lng, Date time){
        String email = "dummyemail@email.com";
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = dateformat.parse("01/01/1970");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = date;
        this.latitude = lat;
        this.longitude = lng;
        this.time = time;
    }


    public Friend(String id, String name,String email, Date dob){
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
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
    public Friend(String id, String name,String email, Date dob, Double latitude, Double longitude){
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
