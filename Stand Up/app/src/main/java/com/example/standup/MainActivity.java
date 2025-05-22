package com.example.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;
    private PendingIntent notifyPendingIntent;
    
    // Changed from INTERVAL_FIFTEEN_MINUTES to 10 seconds (10000 milliseconds)
    private static final long TEN_SECONDS = 10000;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Intent intent;
    private Intent notifyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                        PERMISSION_REQUEST_CODE);
            }
        }

        notifyIntent = new Intent(this, AlarmReceiver.class);

        // Initialize NotificationManager
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        // Create the notification channel
        createNotificationChannel();

        // Initialize AlarmManager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create the notification intent
        notifyPendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        // Check if alarm is already set
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);

        // Get the ToggleButton
        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);
        alarmToggle.setChecked(alarmUp);

        // Set up the ToggleButton listener
        alarmToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Set the alarm to trigger in 10 seconds
                long triggerTime = SystemClock.elapsedRealtime() + TEN_SECONDS;
                
                // Use exact alarm for more accurate timing
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime,
                                notifyPendingIntent);
                    } else {
                        Toast.makeText(this, "Exact alarms not allowed", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                        return;
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime,
                            notifyPendingIntent);
                }
                
                // Schedule the next alarm
                scheduleNextAlarm(triggerTime);
                
                Toast.makeText(this, R.string.alarm_on, Toast.LENGTH_SHORT).show();
            } else {
                // Cancel the alarm
                alarmManager.cancel(notifyPendingIntent);
                mNotificationManager.cancelAll();
                Toast.makeText(this, R.string.alarm_off, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void scheduleNextAlarm(long previousTriggerTime) {
        long nextTriggerTime = previousTriggerTime + TEN_SECONDS;
        
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID, "Stand Up Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Notifications for Stand Up app");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}