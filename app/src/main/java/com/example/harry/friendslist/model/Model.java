package com.example.harry.friendslist.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jay on 1/09/2017.
 */

public class Model {

    private Model model;
    private CurrentUser currentUser;

    public Model createModel(){
        if(model == null){
            model = new Model();
            return model;
        }else{
            return model;
        }
    }

}
