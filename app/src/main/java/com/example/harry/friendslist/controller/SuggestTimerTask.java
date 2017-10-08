package com.example.harry.friendslist.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.harry.friendslist.MainActivity;

import java.util.TimerTask;

/**
 * Created by harry on 9/10/2017.
 */

public class SuggestTimerTask extends TimerTask {

    MainActivity mainActivity;
    public SuggestTimerTask(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        AsyncDistanceDemand asyncDistanceDemand = new AsyncDistanceDemand(mainActivity, null);
        asyncDistanceDemand.execute();
    }
}
