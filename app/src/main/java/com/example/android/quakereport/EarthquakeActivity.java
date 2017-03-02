/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<SeismicData>> {

    private static final int EARTHQUAKE_LOADER_ID = 0;

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    EarthquakeAdapter mAdapter;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of seismicData
        mAdapter = new EarthquakeAdapter(this, new ArrayList<SeismicData>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        //verify network state
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //prompt if no network state
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)){
            //all views that require change
            ListView view = (ListView) findViewById(R.id.list);
            TextView textView = (TextView) findViewById(R.id.empty_list_item);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

            //change textView to warn user of no internet connection
            textView.setText(R.string.noInternet);

            //make list view and progressbar disappear
            progressBar.setVisibility(View.GONE);
            view.setEmptyView(textView);
        }
        else{
            //initiate background thread
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null,this);
        }
    }

    //load menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //config menu item select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //imitates Background thread if not started
    @Override
    public Loader<List<SeismicData>> onCreateLoader(int i, Bundle bundle) {

        //grab preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //magnitude preference
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        //order preference
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        //create URI from URL
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //add to URI URL
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        //starts background process
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    //updates UI
    @Override
    public void onLoadFinished(Loader<List<SeismicData>> loader, List<SeismicData> seismicData) {
        ListView view = (ListView) findViewById(R.id.list);
        TextView textView = (TextView) findViewById(R.id.empty_list_item);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        progressBar.setVisibility(View.GONE);
        if(seismicData.isEmpty()){
            view.setEmptyView(textView);
            }
        else {
            mAdapter.clear();
            mAdapter.addAll(seismicData);
        }
    }

    //clears UI
    @Override
    public void onLoaderReset(Loader<List<SeismicData>> loader) {
        mAdapter.clear();
    }


    /**
     * {@link AsyncTaskLoader} to perform the network request on a background thread.
     */
    private static class EarthquakeLoader extends AsyncTaskLoader<List<SeismicData>> {
        String mUrl;

        public EarthquakeLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<SeismicData> loadInBackground() {
            // Create URL object
            URL url = QueryUtils.createUrl(mUrl);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = QueryUtils.makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
                Log.e(LOG_TAG, "Problem with making HTTP connection");

            }
            //If HTTP request does'nt return values
            if (jsonResponse.equals(null) || jsonResponse.equals("")){
                Log.e(LOG_TAG, "json.Response.equals =  " + jsonResponse);
                return new ArrayList<SeismicData>();}

            // Extract relevant fields from the JSON response and create an {@link Event} object
            List<SeismicData> earthquakes = QueryUtils.extractFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquakes;
        }
    }
}
