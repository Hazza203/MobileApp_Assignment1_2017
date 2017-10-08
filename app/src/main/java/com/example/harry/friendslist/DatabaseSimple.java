package com.example.harry.friendslist;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.harry.friendslist.model.Friend;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Jay on 2/10/2017.
 */


public class DatabaseSimple extends Activity{

    private static final String LOG_TAG = DatabaseSimple.class
            .getName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "assignment2.db";

    protected static final String TABLE_USER = "tbl_users";

    protected static final String TABLE_MEETING = "tbl_meetings";

    protected static final String TABLE_USERFRIEND = "tbl_userfriends";

    protected static final String TABLE_USERMEETING = "tbl_usermeetings";

    protected static final String TABLE_LOCATION = "tbl_location";

    protected static final String CREATE_USER_TABLE = "CREATE TABLE "+TABLE_USER+" (userid INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT, email TEXT, dob DATE, latitude DOUBLE, longitude DOUBLE);";

    protected static final String CREATE_MEETING_TABLE = "CREATE TABLE "+TABLE_MEETING+" (meetingid INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT, startTime TEXT, endTime TEXT, latitude DOUBLE, longitude DOUBLE);";

    protected static final String CREATE_USERMEETING_TABLE = "CREATE TABLE "+TABLE_USERMEETING+" (userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, meetingid INTEGER NOT NULL CONSTRAINT meetingid REFERENCES tbl_meetings(meetingid) ON DELETE CASCADE, title TEXT, startTime TEXT, endTime TEXT, latitude DOUBLE, longitude DOUBLE, PRIMARY KEY (userid,meetingid));";

    protected static final String CREATE_USERFRIEND_TABLE = "CREATE TABLE "+TABLE_USERFRIEND+" (userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, friendid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, PRIMARY KEY (userid,friendid));";

    protected static final String CREATE_LOCATION_TABLE = "CREATE TABLE "+TABLE_LOCATION+" (locationid INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, friendid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, PRIMARY KEY (userid,friendid));";

    protected static final String DROP_TABLE_USER = "DROP TABLE tbl_users";

    protected static final String DROP_TABLE_MEETING = "DROP TABLE tbl_meetings";

    protected static final String DROP_TABLE_USERFRIEND = "DROP TABLE tbl_userfriends";

    protected static final String DROP_TABLE_USERMEETING = "DROP TABLE tbl_usermeetings";

    protected static final String DROP_TABLE_LOCATION = "DROP TABLE tbl_location";

    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_database);

        new Thread(new Runnable()
        {
            public void run()
            {
                // The bulk of our example is here
                runDatabaseExample();
            }
        }).start();
    }
    public void runDatabaseExample() {

        Log.i(LOG_TAG, "Begin Simple Database Example");

        if (Arrays.binarySearch(databaseList(), DATABASE_NAME) >= 0) {
            // Delete the old database file, if it exists
            deleteDatabase(DATABASE_NAME);
        }

        // create a new database
        mDatabase = openOrCreateDatabase(DATABASE_NAME,
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        // SET SOME DATABASE CONFIGURATION INFO
        mDatabase.setLocale(Locale.getDefault()); // Set the locale
        mDatabase.setVersion(1); // Sets the database version.

        Log.i(LOG_TAG, "Created database: " + mDatabase.getPath());
        Log.i(LOG_TAG, "Database Version: " + mDatabase.getVersion());
        Log.i(LOG_TAG, "Database Page Size: " + mDatabase.getPageSize());
        Log.i(LOG_TAG, "Database Max Size: " + mDatabase.getMaximumSize());

        Log.i(LOG_TAG, "Database Open?  " + mDatabase.isOpen());
        Log.i(LOG_TAG, "Database readonly?  " + mDatabase.isReadOnly());
        Log.i(LOG_TAG,
                "Database Locked by current thread?  "
                        + mDatabase.isDbLockedByCurrentThread());

        mDatabase.execSQL(CREATE_USER_TABLE);
        mDatabase.execSQL(CREATE_MEETING_TABLE);
        mDatabase.execSQL(CREATE_USERMEETING_TABLE);
        mDatabase.execSQL(CREATE_USERFRIEND_TABLE);
        mDatabase.execSQL(CREATE_LOCATION_TABLE);

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dob = null;

        try {
            dob = dateformat.parse("01/01/2000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Friend newFriend = new Friend("-1","test","test@test.test",dob,138.0000, -142.000,dob);

        ContentValues values = new ContentValues();
        values.put("name", newFriend.getName());
        values.put("email", newFriend.getEmail());
        values.put("dob", DateFormat.getInstance().format(newFriend.getBirthday()));
        values.put("email", newFriend.getEmail());
        values.put("latitude", newFriend.getLatitude());
        values.put("longitude", newFriend.getLongitude());

        newFriend.setId(Long.toString( mDatabase.insertOrThrow(CREATE_USER_TABLE, null, values)));

        Cursor c = mDatabase
                .query(TABLE_USER, null, null, null, null, null, null);
        LogCursorInfo(c);
        c.close();
    }
    public void LogCursorInfo(Cursor c)
    {
        Log.i(LOG_TAG, "*** Cursor Begin *** " + " Results:" + c.getCount()
                + " Columns: " + c.getColumnCount());

        // Print column names
        String rowHeaders = "|| ";
        for (int i = 0; i < c.getColumnCount(); i++)
        {

            rowHeaders = rowHeaders.concat(c.getColumnName(i) + " || ");
        }
        Log.i(LOG_TAG, "COLUMNS " + rowHeaders);

        // Print records
        c.moveToFirst();
        while (c.isAfterLast() == false)
        {
            String rowResults = "|| ";
            for (int i = 0; i < c.getColumnCount(); i++)
            {
                rowResults = rowResults.concat(c.getString(i) + " || ");
            }
            Log.i(LOG_TAG, "Row " + c.getPosition() + ": " + rowResults);

            c.moveToNext();
        }
        Log.i(LOG_TAG, "*** Cursor End ***");
    }
}
