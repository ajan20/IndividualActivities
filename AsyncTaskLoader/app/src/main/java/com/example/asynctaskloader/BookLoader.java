package com.example.asynctaskloader;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BookLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "BookLoader";
    private final String mQueryString;

    public BookLoader(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = java.net.URLEncoder.encode(mQueryString, "UTF-8");
            URL requestURL = new URL("https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery);

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            jsonResponse = convertStreamToString(inputStream);
            
            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray items = jsonObject.getJSONArray("items");
            if (items.length() > 0) {
                JSONObject book = items.getJSONObject(0);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authors = volumeInfo.optJSONArray("authors");
                String author = authors != null && authors.length() > 0 ? authors.getString(0) : "Unknown Author";
                
                return title + " [" + author + "]";
            }
            
            return "No results found";

        } catch (Exception e) {
            Log.e(TAG, "Error fetching book data", e);
            return "Error loading book data: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
} 