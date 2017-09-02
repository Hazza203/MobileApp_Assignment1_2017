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
    public void setCurrentUserString(String id,String userName, String password, String name, String email, Date dob){
        // set these
        Double longitude = Double.NaN;
        Double latitude = Double.NaN;
        currentUser = new CurrentUser(id,userName,password,name,email,dob,longitude,latitude);
    }
    public List<Double> getCurrentLocation(){
        // idk how we are getting the this
        return null;
    }
}
