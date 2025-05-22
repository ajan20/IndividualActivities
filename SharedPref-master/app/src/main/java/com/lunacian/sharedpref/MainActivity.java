package com.lunacian.sharedpref;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int mCount = 0;
    // Current background color
    private int mColor;
    // Text view to display both count and color
    private TextView mShowCountTextView;

    // Key for current count
    private final String COUNT_KEY = "count";
    // Key for current color
    private final String COLOR_KEY = "color";
    // Name of shared preferences file
    private static final String PREFS_NAME = "SharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views, color
        mShowCountTextView = findViewById(R.id.count_textview);
        mColor = ContextCompat.getColor(this,
                R.color.default_background);

        // Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Restore the saved state from SharedPreferences
        mCount = sharedPreferences.getInt(COUNT_KEY, 0);
        mColor = sharedPreferences.getInt(COLOR_KEY, mColor);

        // Update the UI with the saved values
        mShowCountTextView.setText(String.format("%s", mCount));
        mShowCountTextView.setBackgroundColor(mColor);
    }

    public void changeBackground(View view) {
        int color = ((ColorDrawable) view.getBackground()).getColor();
        mShowCountTextView.setBackgroundColor(color);
        mColor = color;
        // Save the color to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(COLOR_KEY, mColor);
        editor.apply();
    }

    public void countUp(View view) {
        mCount++;
        mShowCountTextView.setText(String.format("%s", mCount));
        // Save the count to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(COUNT_KEY, mCount);
        editor.apply();
    }

    public void reset(View view) {
        // Reset count
        mCount = 0;
        mShowCountTextView.setText(String.format("%s", mCount));

        // Reset color
        mColor = ContextCompat.getColor(this,
                R.color.default_background);
        mShowCountTextView.setBackgroundColor(mColor);

        // Save the reset values to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(COUNT_KEY, mCount);
        editor.putInt(COLOR_KEY, mColor);
        editor.apply();
    }
}