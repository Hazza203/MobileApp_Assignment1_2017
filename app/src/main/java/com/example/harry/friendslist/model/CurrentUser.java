package com.example.harry.friendslist.model;

import com.example.harry.friendslist.interfaces.FriendInterface;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jay on 3/09/2017.
 */

public class CurrentUser extends Friend implements FriendInterface {

    private String userName;
    private String password;

    private List<Friend> friends = new LinkedList<>();

    public CurrentUser(String id,String userName, String password, String name, String email, Date dob, Double latitude, Double longitude){
        super(id,name,email,dob,latitude,longitude);
        this.userName = userName;
        this.password = password;
    }

    public void setUsername(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public boolean testPassword(String password){
        if(password.equals(this.password)){
            return true;
        }
        return false;
    }
    public void addAFriend(String id, String name,String email, Date dob){
        Friend newFriend = new Friend(id,name,email,dob);
        if(!friends.contains(newFriend))
            friends.add(newFriend);
    }
    public void removeAFriend(Friend removeThisOne){
        if(friends.contains(removeThisOne))
            friends.remove(removeThisOne);
    }
    public List<Friend> getFriendsList(){
        return friends;
    }
}
