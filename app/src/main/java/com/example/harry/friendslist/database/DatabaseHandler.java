package com.example.harry.friendslist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Meeting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jay on 9/10/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHandler.class
            .getName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "assignment2.db";

    protected static final String TABLE_USER = "tbl_users";

    protected static final String TABLE_MEETING = "tbl_meetings";

    protected static final String TABLE_USERFRIEND = "tbl_userfriends";

    protected static final String TABLE_USERMEETING = "tbl_usermeetings";

    protected static final String TABLE_LOCATION = "tbl_location";

    protected static final String CREATE_USER_TABLE = "CREATE TABLE "+TABLE_USER+" (userid INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT, email TEXT, dob DATE);";

    protected static final String CREATE_MEETING_TABLE = "CREATE TABLE "+TABLE_MEETING+" (meetingid INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT, startTime TEXT, endTime TEXT, latitude DOUBLE, longitude DOUBLE);";

    protected static final String CREATE_USERMEETING_TABLE = "CREATE TABLE "+TABLE_USERMEETING+" (userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, meetingid INTEGER NOT NULL CONSTRAINT meetingid REFERENCES tbl_meetings(meetingid) ON DELETE CASCADE, PRIMARY KEY (userid,meetingid));";

    protected static final String CREATE_USERFRIEND_TABLE = "CREATE TABLE "+TABLE_USERFRIEND+" (userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, friendid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, PRIMARY KEY (userid,friendid));";

    protected static final String CREATE_LOCATION_TABLE = "CREATE TABLE "+TABLE_LOCATION+" (locationid INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER NOT NULL CONSTRAINT userid REFERENCES tbl_users(userid) ON DELETE CASCADE, latitude DOUBLE, longitude DOUBLE));";

    protected static final String DROP_TABLE_USER = "DROP TABLE tbl_users";

    protected static final String DROP_TABLE_MEETING = "DROP TABLE tbl_meetings";

    protected static final String DROP_TABLE_USERFRIEND = "DROP TABLE tbl_userfriends";

    protected static final String DROP_TABLE_USERMEETING = "DROP TABLE tbl_usermeetings";

    protected static final String DROP_TABLE_LOCATION = "DROP TABLE tbl_location";

    // Caspar: The following is no longer true as of Android 2.2 (see
    // http://www.sqlite.org/lang.html)
    // Using triggers to enforce foreign key constraint, because Sqlite doesn't
    // enforce them
    private static final String CREATE_TRIGGER_ADD = "CREATE TRIGGER fk_insert_book BEFORE INSERT ON tbl_books FOR EACH ROW BEGIN  SELECT RAISE(ROLLBACK, 'insert on table \"tbl_books\" violates foreign key constraint \"fk_authorid\"') WHERE  (SELECT id FROM tbl_authors WHERE id = NEW.authorid) IS NULL; END;";

    private static final String CREATE_TRIGGER_UPDATE = "CREATE TRIGGER fk_update_book BEFORE UPDATE ON tbl_books FOR EACH ROW BEGIN SELECT RAISE(ROLLBACK, 'update on table \"tbl_books\" violates foreign key constraint \"fk_authorid\"') WHERE  (SELECT id FROM tbl_authors WHERE id = NEW.authorid) IS NULL; END;";

    private static final String CREATE_TRIGGER_DELETE = "CREATE TRIGGER fk_delete_author BEFORE DELETE ON tbl_authors FOR EACH ROW BEGIN SELECT RAISE(ROLLBACK, 'delete on table \"tbl_authors\" violates foreign key constraint \"fk_authorid\"') WHERE (SELECT authorid FROM tbl_books WHERE authorid = OLD.id) IS NOT NULL; END;";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MEETING_TABLE);
        db.execSQL(CREATE_USERMEETING_TABLE);
        db.execSQL(CREATE_USERFRIEND_TABLE);
        //db.execSQL(CREATE_LOCATION_TABLE);
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
                cursor.getString(1), cursor.getString(2), dob);

        return returnFriend;
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
