package com.example.roomwordssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NewWordActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private EditText editWordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);
        editWordView = findViewById(R.id.edit_word);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editWordView.getText())) {
                setResult(Activity.RESULT_CANCELED, replyIntent);
            } else {
                String word = editWordView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, word);
                setResult(Activity.RESULT_OK, replyIntent);
            }
            finish();
        });
    }
} 