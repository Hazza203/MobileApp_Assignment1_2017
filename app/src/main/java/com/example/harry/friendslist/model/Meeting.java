package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.MeetingInterface;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by harry on 3/09/2017.
 */

public class Meeting extends MeetingAbstract implements MeetingInterface {



    public Meeting (String id, String title, String startTime, String endTime, List<Friend> friends, LatLng latLng){
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTIme = endTime;
        this.friends = friends;
        this.latLng = latLng;
    }

    @Override
    public void addFriend(Friend friend) {
        super.addFriend(friend);
        friends.add(friend);
    }

    @Override
    public void removeFriend(Friend friend) {
        super.removeFriend(friend);
        if(friends.contains(friend)){
            friends.remove(friend);
        }
    }
}
