package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by FreakPirate on 1/31/2016.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
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

        return rootView;
    }

    class FetchWeatherTask extends AsyncTask <Void, Void, Void>{

        private String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=462001,In&mode=json&units=metric&cnt=7";
        private String API_KEY = "&APPID=" + BuildConfig.API_KEY;
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            /* HTTP REQUEST */

            //These two need to be declared outside the try/catch
            //so that they can be closed in finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //will contains the raw JSON response as a string
            String forecastJsonStr = null;

            try{
                //Construct the URL for openweathermap.org query
                URL url = new URL(baseURL.concat(API_KEY));

                //create a request to openweathermap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream to a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null){
                    //Nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    //Since it's a JSON, adding a newline isn't necessary
                    //But it does make debugging a lot easier if you print out
                    //the completed buffer for debugging

                    buffer.append(line + '\n');
                }

                if(buffer.length() == 0){
                    //Stream was empty.
                    //No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON String: " + forecastJsonStr);

            }catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                //If the code didn't successfully get the weather data
                //there's no point in attempting to parse it.
                return null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            /* END */
            return null;
        }
    }
}