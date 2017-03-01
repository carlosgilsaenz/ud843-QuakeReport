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
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<SeismicData>> {

    private static final int EARTHQUAKE_LOADER_ID = 0;

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    EarthquakeAdapter mAdapter;

    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
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

        //initiate background thread
        getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null,this);
    }

    //imitates Background thread if not started
    @Override
    public Loader<List<SeismicData>> onCreateLoader(int i, Bundle bundle) {
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    //updates UI
    @Override
    public void onLoadFinished(Loader<List<SeismicData>> loader, List<SeismicData> seismicData) {
        ListView view = (ListView) findViewById(R.id.list);
        TextView textView = (TextView) findViewById(R.id.empty_list_item);

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
            if (jsonResponse.equals(null) || jsonResponse.equals("")){return new ArrayList<SeismicData>();}

            // Extract relevant fields from the JSON response and create an {@link Event} object
            List<SeismicData> earthquakes = QueryUtils.extractFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquakes;
        }
    }
}
