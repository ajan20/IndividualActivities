package com.example.asynchtask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView messageTextView;
    private Button startTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = findViewById(R.id.messageTextView);
        startTaskButton = findViewById(R.id.startTaskButton);

        startTaskButton.setOnClickListener(v -> new SleepAsyncTask().execute());
    }

    private class SleepAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            messageTextView.setText("Going to sleep...");
            startTaskButton.setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            Random random = new Random();
            int sleepTime = random.nextInt(2500) + 500; // Sleep between 500-3000ms
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Awake at last after sleeping for " + sleepTime + " milliseconds!";
        }

        @Override
        protected void onPostExecute(String result) {
            messageTextView.setText(result);
            startTaskButton.setEnabled(true);
        }
    }
}