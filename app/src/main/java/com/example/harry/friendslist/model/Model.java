package com.example.harry.friendslist.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jay on 1/09/2017.
 */

public class Model {

    private Model model;
    private List<Friend> friends = new LinkedList<>();

    public Model createModel(){
        if(model == null){
            model = new Model();
            return model;
        }else{
            return model;
        }
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
    public void setDumbyData(){

    }
}
