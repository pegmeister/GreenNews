package com.example.pegmeister.greennews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /**
     * URL for Pollution news from the Guardian news database
     */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    /**
     * Constant value for news loader ID
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * Adapter for the list of news
     */
    private NewsAdapter newsAdapter;
    /**
     * TextView setup when list is empty
     */
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        // Create an adapter for to take News as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set ListView to NewsAdapter
        newsListView.setAdapter(newsAdapter);

        // Set an item click listener on the ListView, which sends an intent to web browser to display more details
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // find the current news user clicked on
                News currentNews = newsAdapter.getItem(i);

                // convert the String URL into a URI object to pass into the intent constructor
                Uri newsUrl = Uri.parse(currentNews.getNewsUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        // onCreateLoader instantiates and returns a new Loader for the given ID
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves the keyword search and orderBy value
        String keyword = sharedPrefs.getString(
                getString(R.string.settings_search_keyword_key),
                getString(R.string.settings_search_keyword_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("page-size", "15");
        uriBuilder.appendQueryParameter("q", keyword);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "bae3f33c-1167-49fd-92ff-ec2c959122cc");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        emptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        newsAdapter.clear();

        // If there is a valid list of {@link New}s, then add them to the adapter's data set.
        // This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
        Log.v("Loader Reset", "confirmed");
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options menu specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever on item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}





