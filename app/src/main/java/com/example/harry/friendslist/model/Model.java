package com.example.harry.friendslist.model;


import android.content.Context;

import com.example.harry.friendslist.database.DatabaseHandler;

import java.util.Date;


/**
 * Created by Jay on 1/09/2017.
 */

public class Model {

    private static Model instance;
    //private static DBOpenHelper db;
    private String LOG_TAG = this.getClass().getName();
    private CurrentUser currentUser;
    private Context context;
    private DatabaseHandler db;

    private Model() {

    }
    static {
        instance = new Model();
    }

    public static Model getInstance(){
        return instance;
    }

    public void setCurrentUserString(String id, String userName, String password, String name, String email, Date dob, Date time, Context context, DatabaseHandler db){
        // set these
        Double longitude = Double.NaN;
        Double latitude = Double.NaN;
        this.context = context;
        this.db = db;
        currentUser = new CurrentUser(id,userName,password,name,email,dob,longitude,latitude, time,context, db);

    }


    public CurrentUser getCurrentUser(){
        return currentUser;
    }

    public void startDatabase(){
        //DBOpenHelper.getSingletonInstance(context);

    }
}
