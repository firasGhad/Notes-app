package com.example.notes;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();//create the notification channel
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
//create the notification channel
        getManager().createNotificationChannel(channel);//to build the notification for real
    }

    public NotificationManager getManager() {//to build the notification for real
        if (mManager == null) {//if the notification manager is null create new one else use the existed one
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {//build the notification
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Alarm!")
                .setContentText("Your time is Up.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setVibrate(new long[] { 1000, 1000})
                .setSound( Settings.System.DEFAULT_NOTIFICATION_URI);
    }
}