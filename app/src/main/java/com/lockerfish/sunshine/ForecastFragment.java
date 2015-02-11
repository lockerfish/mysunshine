package com.lockerfish.sunshine;

import android.os.Bundle;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * A forecast fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

	private static final String TAG = "ForecastFragment";

	private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Wed - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 65/56",
                "Sat - TRAPPED IN WEATHERSTATION - 60/51",
                "Sun - Sunny - 80/68"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        mForecastAdapter = new ArrayAdapter(
        	getActivity(), 
        	R.layout.list_item_forecast, 
        	R.id.list_item_forecast_textview,
        	weekForecast);

        ListView list = (ListView)rootView.findViewById(R.id.listview_forecast);
        list.setAdapter(mForecastAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        	@Override
        	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        		String item = (String)adapterView.getItemAtPosition(i);

        		// Toast toast = Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT);
        		// toast.show();

        		Intent intent = new Intent(getActivity(), DetailActivity.class);
        		intent.putExtra(Intent.EXTRA_TEXT, item);
        		startActivity(intent);

        	}
        });

        return rootView;
    }

     @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
		    FetchWeatherTask asyncTask = new FetchWeatherTask();
		    asyncTask.execute("12540,usa");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

		/* The date/time conversion code is going to be moved outside the asynctask later,
		 * so for convenience we're breaking it out into its own method now.
		 */
		private String getReadableDateString(long time){
		    // Because the API returns a unix timestamp (measured in seconds),
		    // it must be converted to milliseconds in order to be converted to valid date.
		    Date date = new Date(time * 1000);
		    SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
		    return format.format(date).toString();
		}
		 
		/**
		 * Prepare the weather high/lows for presentation.
		 */
		private String formatHighLows(double high, double low) {
		    // For presentation, assume the user doesn't care about tenths of a degree.
		    long roundedHigh = Math.round(high);
		    long roundedLow = Math.round(low);
		 
		    String highLowStr = roundedHigh + "/" + roundedLow;
		    return highLowStr;
		}
		 
		/**
		 * Take the String representing the complete forecast in JSON Format and
		 * pull out the data we need to construct the Strings needed for the wireframes.
		 *
		 * Fortunately parsing is easy:  constructor takes the JSON string and converts it
		 * into an Object hierarchy for us.
		 */
		private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
		        throws JSONException {
		 
		    // These are the names of the JSON objects that need to be extracted.
		    final String OWM_LIST = "list";
		    final String OWM_WEATHER = "weather";
		    final String OWM_TEMPERATURE = "temp";
		    final String OWM_MAX = "max";
		    final String OWM_MIN = "min";
		    final String OWM_DATETIME = "dt";
		    final String OWM_DESCRIPTION = "main";
		 
		    JSONObject forecastJson = new JSONObject(forecastJsonStr);
		    JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
		 
		    String[] resultStrs = new String[numDays];
		    for(int i = 0; i < weatherArray.length(); i++) {
		        // For now, using the format "Day, description, hi/low"
		        String day;
		        String description;
		        String highAndLow;
		 
		        // Get the JSON object representing the day
		        JSONObject dayForecast = weatherArray.getJSONObject(i);
		 
		        // The date/time is returned as a long.  We need to convert that
		        // into something human-readable, since most people won't read "1400356800" as
		        // "this saturday".
		        long dateTime = dayForecast.getLong(OWM_DATETIME);
		        day = getReadableDateString(dateTime);
		 
		        // description is in a child array called "weather", which is 1 element long.
		        JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
		        description = weatherObject.getString(OWM_DESCRIPTION);
		 
		        // Temperatures are in a child object called "temp".  Try not to name variables
		        // "temp" when working with temperature.  It confuses everybody.
		        JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
		        double high = temperatureObject.getDouble(OWM_MAX);
		        double low = temperatureObject.getDouble(OWM_MIN);
		 
		        highAndLow = formatHighLows(high, low);
		        resultStrs[i] = day + " - " + description + " - " + highAndLow;
		    }
		 
		    return resultStrs;
		}

    	@Override
    	protected String[] doInBackground(String... params) {

			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			 
			// Will contain the raw JSON response as a string.
			String forecastJsonStr = null;

			String format = "json";
			String units = "metric";
			int numDays = 7;
			 
			try {

			    // Construct the URL for the OpenWeatherMap query
			    // Possible parameters are available at OWM's forecast API page, at
			    // http://openweathermap.org/API#forecast

			    final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
			    final String QUERY_PARAM = "q";
			    final String FORMAT_PARAM = "mode";
			    final String UNITS_PARAM = "units";
			    final String DAYS_PARAM = "cnt";
			    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
			        .appendQueryParameter(QUERY_PARAM, params[0])
			        .appendQueryParameter(FORMAT_PARAM, format)
			        .appendQueryParameter(UNITS_PARAM, units)
			        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
			        .build();

			    URL url = new URL(builtUri.toString());

			    // Create the request to OpenWeatherMap, and open the connection
			    urlConnection = (HttpURLConnection) url.openConnection();
			    urlConnection.setRequestMethod("GET");
			    urlConnection.connect();
			 
			    // Read the input stream into a String
			    InputStream inputStream = urlConnection.getInputStream();
			    StringBuffer buffer = new StringBuffer();
			    if (inputStream == null) {
			        // Nothing to do.
			        forecastJsonStr = null;
			    }
			    reader = new BufferedReader(new InputStreamReader(inputStream));
			 
			    String line;
			    while ((line = reader.readLine()) != null) {
			        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
			        // But it does make debugging a *lot* easier if you print out the completed
			        // buffer for debugging.
			        buffer.append(line + "\n");
			    }
			 
			    if (buffer.length() == 0) {
			        // Stream was empty.  No point in parsing.
			        forecastJsonStr = null;
			    }
			    forecastJsonStr = buffer.toString();
			    
			    return getWeatherDataFromJson(forecastJsonStr, numDays);
			} catch (IOException e) {
			    Log.e(TAG, "Error ", e);

			    // If the code didn't successfully get the weather data, there's no point in attempting
			    // to parse it.
			    forecastJsonStr = null;
			} catch (JSONException je) {
				Log.e(TAG, "Error", je);
				forecastJsonStr = null;
			} finally{
			    if (urlConnection != null) {
			        urlConnection.disconnect();
			    }
			    if (reader != null) {
			        try {
			            reader.close();
			        } catch (final IOException e) {
			            Log.e(TAG, "Error closing stream", e);
			        }
			    }
			}

			return null;
    	}


    	@Override
    	public void onPostExecute(String[] forecast) {

    		if (forecast != null) {
    			mForecastAdapter.clear();
    			for (String dayForecastStr : forecast) {
    				mForecastAdapter.add(dayForecastStr);
    			}    			
    		}
    	}

    } 
}