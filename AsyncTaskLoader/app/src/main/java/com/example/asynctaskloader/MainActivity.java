package com.example.asynctaskloader;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final int BOOK_LOADER_ID = 1;
    private EditText mBookInput;
    private TextView mResultsText;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mResultsText = findViewById(R.id.resultsText);
        mLoadingIndicator = findViewById(R.id.loadingIndicator);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            String queryString = mBookInput.getText().toString();
            if (queryString.length() == 0) {
                mResultsText.setText("Please enter a search term");
                return;
            }

            mLoadingIndicator.setVisibility(View.VISIBLE);
            mResultsText.setText("");

            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);

            LoaderManager loaderManager = LoaderManager.getInstance(this);
            if (loaderManager.getLoader(BOOK_LOADER_ID) != null) {
                loaderManager.restartLoader(BOOK_LOADER_ID, queryBundle, this);
            } else {
                loaderManager.initLoader(BOOK_LOADER_ID, queryBundle, this);
            }
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        mLoadingIndicator.setVisibility(View.GONE);
        mResultsText.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}