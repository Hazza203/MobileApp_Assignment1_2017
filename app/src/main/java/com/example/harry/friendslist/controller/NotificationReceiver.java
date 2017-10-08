package com.example.harry.friendslist.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.harry.friendslist.MainActivity;
import com.example.harry.friendslist.R;

/**
 * Created by harry on 8/10/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private String LOG_TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String startTime = intent.getStringExtra("start");
        String endTime = intent.getStringExtra("end");

        Intent serviceIntent = new Intent(context, RemindLaterService.class);
        serviceIntent.putExtra("title", title);
        serviceIntent.putExtra("start", startTime);
        serviceIntent.putExtra("end", endTime);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_schedule_black_24dp)
                .setContentText(startTime + " - " + endTime)
                .setContentTitle(title)
                .addAction(new NotificationCompat.Action(R.mipmap.ic_settings_black_24dp, "Remind me later", pendingIntent))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        Log.i(LOG_TAG, "Doing service");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
