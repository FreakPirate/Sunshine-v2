package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by FreakPirate on 1/31/2016.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This is used to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if (id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Fake data as an array of string
        String [] forecast_array = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Rainy - 64/51",
                "Fri - Foggy - 70/46",
                "Sat - Sunny - 76/68",
                "Sun - Foggy - 67/46"
        };

        //Fake data converted to ArrayList of string
        ArrayList<String> weekForecast = new ArrayList<String>(
                Arrays.asList(forecast_array));

        //ArrayAdapter is a mediator between ListView and list_item_layout
        //It handles the creation of List item upon request by ListView
        //As user either scroll down or up
        //It automates the process of item creation
        mForecastAdapter = new ArrayAdapter<String>(
                //Context
                getActivity(),
                //ListItem Layout (xml file)
                R.layout.list_item_forecast,
                //textview created in the list_item_forecast
                R.id.list_item_forecast_textview,
                //Data to bind with list_item_forecast_textview
                weekForecast
        );

        //ViewGroups are searched by traversing the id hierarchy tree
        //since ListView is the child of rootView, we can use "findViewVyId" of rootView
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        //Finally bind previously created ArrayAdapter to the listview
        //so that it can request for new item
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String forecast = adapterView.getItemAtPosition(i).toString();
//
//                Toast toast = Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT);
//                toast.show();

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask(this, getActivity());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String postalCode = sharedPrefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value));

        weatherTask.execute(postalCode);
    }

    private void openPreferredLocationInMap(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value)
        );

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

}