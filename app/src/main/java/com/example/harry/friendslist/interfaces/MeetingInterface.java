package com.example.harry.friendslist.interfaces;

import com.example.harry.friendslist.model.Friend;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
/**
 * Created by Jay on 1/09/2017.
 */

public interface MeetingInterface {

    String getID();
    void setID(String id);

    String getTitle();
    void setTitle(String Title);

    String getStartTime();
    void setStartTime(String time);

    String getEndTime();
    void setEndTime(String time);

    LatLng getLocation();
    void setLocation(LatLng latlng);

    void addFriend(Friend friend);
    void removeFriend(Friend friend);
    List<Friend> getInvited();
}
