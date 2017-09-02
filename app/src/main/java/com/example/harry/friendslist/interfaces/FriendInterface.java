package com.example.harry.friendslist.interfaces;

import java.util.Date;

/**
 * Created by Jay on 1/09/2017.
 */

public interface FriendInterface {

    String getId();
    void setId(String id);

    String getName();
    void setName(String name);

    String getEmail();
    void setEmail(String email);

    Date getBirthday();
    void setBirthday(Date dob);

    Double getLongitude();
    void setLongitude(Double longitude);

    Double getLatitude();
    void setLatitude(Double latitude);

}
