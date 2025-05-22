package com.example.notifyme;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Button mNotifyButton;
    private Button mUpdateButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        mNotifyButton = findViewById(R.id.notify_btn);
        mUpdateButton = findViewById(R.id.update_btn);
        mCancelButton = findViewById(R.id.cancel_btn);

        // Set up button click handlers
        mNotifyButton.setOnClickListener(view -> sendNotification());
        mUpdateButton.setOnClickListener(view -> updateNotification());
        mCancelButton.setOnClickListener(view -> cancelNotification());

        // Initialize Notification Manager
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create Notification Channel
        createNotificationChannel();

        // Make buttons initially enabled/disabled as appropriate
        setNotificationButtonState(true, false, false);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Notify Me Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Notify Me App");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    @SuppressLint("NotificationPermission")
    private void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    @SuppressLint("NotificationPermission")
    private void updateNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setContentText("This is your updated notification text!");
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    private void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    private void setNotificationButtonState(Boolean isNotifyEnabled,
                                         Boolean isUpdateEnabled,
                                         Boolean isCancelEnabled) {
        mNotifyButton.setEnabled(isNotifyEnabled);
        mUpdateButton.setEnabled(isUpdateEnabled);
        mCancelButton.setEnabled(isCancelEnabled);
    }
}