package com.example.pegmeister.greennews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Empty constructor
     */
    private QueryUtils() {
    }

    // setup fetchData method
    public static List<News> fetchData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        // Extract relevant fields from JSON response and create a list of {@link News}
        List<News> greenNews = extractFeatureFromJson(jsonResponse);

        // Return the list of news
        return greenNews;
    }

    /**
     * Return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // makeHttpRequest(URL url) method specifies than an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been build up from parsing the given JSON response
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // create an empty ArrayList that we can start adding news to
        List<News> greenNews = new ArrayList<>();

        try {
            // Create a new json response object
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            // Create a new json response object from response
            JSONObject baseJsonResponseResult = baseJsonResponse.getJSONObject("response");
            // Create a new json array from result
            JSONArray currentNewsArray = baseJsonResponseResult.getJSONArray("results");

            // Loop through json response to get data from specific fields
            for (int i = 0; i < currentNewsArray.length(); i++) {
                JSONObject currentNews = currentNewsArray.getJSONObject(i);

                String title = currentNews.getString("webTitle");
                String category = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String pubDate = formatDate(currentNews.getString("webPublicationDate"));

                // Extract the tags array to retrieve author name under webTitle field
                JSONArray authorArray = currentNews.getJSONArray("tags");
                JSONObject currentAuthor = authorArray.getJSONObject(0);
                String author = currentAuthor.getString("webTitle");

                News news = new News(title, category, author, pubDate, url);

                greenNews.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        // Return the list of news
        return greenNews;
    }

    // create a formatDate method to split between date and time, return only date string
    private static String formatDate(String date) {
        return date.substring(0, date.indexOf("T"));
    }
}
