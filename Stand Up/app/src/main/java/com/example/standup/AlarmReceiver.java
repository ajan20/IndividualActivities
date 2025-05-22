package com.example.standup;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final long TEN_SECONDS = 10000;
    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;
    private PendingIntent notifyPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Create the notification intent for the next alarm
        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent, 
                 PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Deliver the notification
        deliverNotification(context);
        
        // Schedule the next alarm
        scheduleNextAlarm(context);
    }
    
    private void scheduleNextAlarm(Context context) {
        long nextTriggerTime = SystemClock.elapsedRealtime() + TEN_SECONDS;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        nextTriggerTime,
                        notifyPendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    nextTriggerTime,
                    notifyPendingIntent);
        }
    }

    private void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        android.app.PendingIntent contentPendingIntent = android.app.PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, 
                 android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stand_up)
                .setContentTitle(context.getString(R.string.stand_up_notification_title))
                .setContentText(context.getString(R.string.stand_up_notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // For Android 13+ (API level 33+), we need to set the category
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            builder.setCategory(NotificationCompat.CATEGORY_REMINDER);
        }

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
} 