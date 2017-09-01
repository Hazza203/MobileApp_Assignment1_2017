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
    }
}
