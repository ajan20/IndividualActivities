package com.example.powerreceiver;

import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private CustomReceiver mReceiver = new CustomReceiver();
    private static final String CUSTOM_ACTION = "com.example.powerreceiver.ACTION_CUSTOM_BROADCAST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register the receiver using the activity context
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        // Register for the custom broadcast
        IntentFilter customFilter = new IntentFilter(CUSTOM_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, customFilter);
    }

    public void sendCustomBroadcast(View view) {
        Intent customBroadcastIntent = new Intent(CUSTOM_ACTION);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(customBroadcastIntent);
    }

    @Override
    protected void onDestroy() {
        // Unregister the receiver
        this.unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}