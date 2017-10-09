package com.example.harry.friendslist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Meeting;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jay on 9/10/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHandler.class
            .getName();

    private static DatabaseHandler instance;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "assignment2.db";

    protected static final String TABLE_USER = "tbl_users";

    protected static final String TABLE_MEETING = "tbl_meetings";

    protected static final String TABLE_USERMEETING = "tbl_usermeetings";

    protected static final String CREATE_USER_TABLE = "CREATE TABLE "+TABLE_USER+" (userid INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT, email TEXT, dob DATE, latitude DOUBLE, longitude DOUBLE);";

    protected static final String CREATE_MEETING_TABLE = "CREATE TABLE "+TABLE_MEETING+" (meetingid INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT, startTime TEXT, endTime TEXT, latitude DOUBLE, longitude DOUBLE);";

    protected static final String CREATE_USERMEETING_TABLE = "CREATE TABLE "+TABLE_USERMEETING+" (userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, meetingid INTEGER NOT NULL CONSTRAINT meetingid REFERENCES tbl_meetings(meetingid) ON DELETE CASCADE, PRIMARY KEY (userid,meetingid));";

    protected static final String DROP_TABLE_USER = "DROP TABLE tbl_users";

    protected static final String DROP_TABLE_MEETING = "DROP TABLE tbl_meetings";

    protected static final String DROP_TABLE_USERFRIEND = "DROP TABLE tbl_userfriends";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MEETING_TABLE);
        db.execSQL(CREATE_USERMEETING_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void addFriend(Friend newUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", newUser.getName());
        values.put("email", newUser.getEmail());
        values.put("dob", DateFormat.getInstance().format(newUser.getBirthday()));
        values.put("latitude", newUser.getLatitude());
        values.put("longitude", newUser.getLongitude());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }
    public void addMeeting(Meeting newMeeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues meeting = new ContentValues();
        ContentValues usermeeting = new ContentValues();
        meeting.put("title", newMeeting.getTitle());
        meeting.put("startTime", newMeeting.getStartTime());
        meeting.put("endTime", newMeeting.getEndTime());
        meeting.put("latitude", newMeeting.getLocation().latitude);
        meeting.put("longitude", newMeeting.getLocation().longitude);

        newMeeting.setID(Long.toString(db.insert(TABLE_MEETING, null, meeting)));
        List<Friend> invited = newMeeting.getInvited();
        for ( int i =0; i < invited.size(); i++ ) {
            usermeeting.put("userid", Integer.parseInt(invited.get(i).getId()));
            usermeeting.put("meetingid", Integer.parseInt(newMeeting.getID()));
            db.insert(TABLE_USERMEETING, null, meeting);
        }
        db.close();
    }
    public void updateMeeting(Meeting newMeeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues meeting = new ContentValues();
        ContentValues usermeeting = new ContentValues();
        meeting.put("title", newMeeting.getTitle());
        meeting.put("startTime", newMeeting.getStartTime());
        meeting.put("endTime", newMeeting.getEndTime());
        meeting.put("latitude", newMeeting.getLocation().latitude);
        meeting.put("longitude", newMeeting.getLocation().longitude);

        newMeeting.setID(Long.toString(db.update(TABLE_MEETING, meeting, "meetingid" + " = ?",
                new String[] { String.valueOf(newMeeting.getID()) })));
        db.close();
    }
    public void dropDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_TABLE_USERFRIEND);
        db.execSQL(DROP_TABLE_USER);
        db.execSQL(DROP_TABLE_MEETING);

        db.close();
    }
    public void addFriendToMeeting(Meeting newMeeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues meeting = new ContentValues();
        ContentValues usermeeting = new ContentValues();
        meeting.put("title", newMeeting.getTitle());
        meeting.put("startTime", newMeeting.getStartTime());
        meeting.put("endTime", newMeeting.getEndTime());
        meeting.put("latitude", newMeeting.getLocation().latitude);
        meeting.put("longitude", newMeeting.getLocation().longitude);

        newMeeting.setID(Long.toString(db.update(TABLE_MEETING, meeting, "meetingid" + " = ?",
                new String[] { String.valueOf(newMeeting.getID()) })));
        db.close();
    }

    public Friend getFriend(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[] { "*" }, "userid" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dob = null;
        try {
            dob = dateFormat.parse(cursor.getString(3));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Friend returnFriend = new Friend(cursor.getString(0),
                cursor.getString(1), Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)),dob);

        return returnFriend;
    }

    public List<Friend> getAllFriends() {
        List<Friend> friendList = new ArrayList<Friend>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date dob = null;
                try {
                    dob = dateFormat.parse(cursor.getString(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Friend newFriend = new Friend(
                    cursor.getString(0),
                    cursor.getString(1),
                    Double.parseDouble(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)), dob
                );

                friendList.add(newFriend);
            } while (cursor.moveToNext());
        }

        // return contact list
        return friendList;
    }
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetingList = new ArrayList<Meeting>();
        List<Friend> friendsList = new ArrayList<Friend>();
        // Select All Query
        String selectQueryUm = "SELECT  * FROM " + TABLE_USERMEETING;
        String selectQueryU = "SELECT  * FROM " + TABLE_MEETING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorUM = db.rawQuery(selectQueryUm, null);
        Cursor cursorU = db.rawQuery(selectQueryU, null);

        // looping through all rows and adding to list
        if (cursorU.moveToFirst()) {
            do {
                if (cursorUM.moveToFirst()) {
                    do {
                        if (cursorU.getString(0).equals(cursorUM.getString(1))) {
                            friendsList.add(getFriend(Integer.parseInt(cursorUM.getString(0))));
                        }
                    } while (cursorUM.moveToNext());
                }
                Meeting newMeeting = new Meeting(
                        cursorU.getString(0), cursorU.getString(1),
                        cursorU.getString(2), cursorU.getString(3),
                        friendsList,
                        new LatLng(Double.parseDouble(cursorU.getString(4)),
                                Double.parseDouble(cursorU.getString(5)))
                );
                meetingList.add(newMeeting);
            } while (cursorU.moveToNext());
        }
        // return contact list
        return meetingList;
    }

    public int getUserCount() {
        int count;

        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        return count;
    }
}
