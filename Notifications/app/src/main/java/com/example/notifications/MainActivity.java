package com.example.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Button notifyButton;
    private Button updateButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if launched via broadcast (to update)
        if (getIntent() != null && "update".equals(getIntent().getStringExtra("action"))) {
            updateNotification();
        }

        // Initialize buttons
        notifyButton = findViewById(R.id.notify_button);
        updateButton = findViewById(R.id.update_button);
        cancelButton = findViewById(R.id.cancel_button);

        notifyButton.setOnClickListener(v -> sendNotification());
        updateButton.setOnClickListener(v -> updateNotification());
        cancelButton.setOnClickListener(v -> cancelNotification());

        createNotificationChannel();

        // Initial button state
        setNotificationButtonState(true, false, false);
    }

    private void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Mascot Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(android.graphics.Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies you about something interesting");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        // Intent to reopen MainActivity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent for update action button
        Intent updateIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                updateIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(R.drawable.ic_update, "Update", updatePendingIntent);
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void updateNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1))
                .setBigContentTitle("Notification Updated!"));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    private void setNotificationButtonState(boolean isNotifyEnabled,
                                            boolean isUpdateEnabled,
                                            boolean isCancelEnabled) {
        notifyButton.setEnabled(isNotifyEnabled);
        updateButton.setEnabled(isUpdateEnabled);
        cancelButton.setEnabled(isCancelEnabled);
    }
}
