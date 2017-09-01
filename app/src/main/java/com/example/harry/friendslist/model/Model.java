package com.example.harry.friendslist;

import com.example.harry.friendslist.interfaces.FriendInterface;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jay on 1/09/2017.
 */

public class Model {

    private Model model;
    private List<FriendInterface> friends = new LinkedList<FriendInterface>;

    public Model createModel(){
        if(model == null){
            model = new Model();
            return model;
        }else{
            return model;
        }
    }
}
