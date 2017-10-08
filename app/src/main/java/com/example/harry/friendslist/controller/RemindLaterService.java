package com.example.harry.friendslist.controller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by harry on 9/10/2017.
 */

public class RemindLaterService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        String name = intent.getStringExtra("name");
        String start = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");

        Intent intentAlarm = new Intent(this, NotificationReceiver.class);
        intentAlarm.putExtra("name", name);
        intentAlarm.putExtra("start", start);
        intentAlarm.putExtra("end", end);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
