package com.lockerfish.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import android.net.Uri;

import com.lockerfish.sunshine.data.WeatherContract.WeatherEntry;
import com.lockerfish.sunshine.data.WeatherContract.LocationEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView started");
        Bundle arguments = getArguments();
        Log.v(TAG, "arguments: " + arguments);
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        Log.v(TAG, "mUri: " + mUri);
        Log.v(TAG, "onCreateView completed");
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated");
        Log.v(TAG, "savedInstanceState: " + savedInstanceState);

        if (mUri == null) {
            String location = Utility.getPreferredLocation(getActivity());
            // Bundle bundle = new Bundle();
            Uri contentUri = WeatherEntry.buildWeatherLocationWithDate(
                    location, System.currentTimeMillis());
            Log.v(TAG, "contentUri: " + contentUri);
            // bundle.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            mUri = contentUri;
        }

        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated done");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "In onCreateLoader");

        // if (args != null) {
        //     mUri = args.getParcelable(DetailFragment.DETAIL_URI);
        // }

        if ( null != mUri) {
            Log.v(TAG, "creating loader");
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        Log.v(TAG, "loader NOT created it is NULL");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));

        long date = data.getLong(COL_WEATHER_DATE);
        boolean isMetric = Utility.isMetric(getActivity());
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);

        String dayName = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
        TextView dayNameView = (TextView) getView().findViewById(R.id.detail_day_textview);
        dayNameView.setText(dayName);

        String monthday = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
        TextView monthdayView = (TextView) getView().findViewById(R.id.detail_date_textview);
        monthdayView.setText(monthday);

        String high = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        TextView highView = (TextView) getView().findViewById(R.id.detail_high_textview);
        highView.setText(high);

        String low = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        TextView lowView = (TextView) getView().findViewById(R.id.detail_low_textview);
        lowView.setText(low);

        ImageView iconView = (ImageView) getView().findViewById(R.id.detail_icon);
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String condition = data.getString(COL_WEATHER_DESC);
        TextView conditionView = (TextView) getView().findViewById(R.id.detail_forecast_textview);
        conditionView.setText(condition);


        // Read humidity from cursor and update view
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        TextView humidityView  = (TextView) getView().findViewById(R.id.detail_humidity_textview);
        humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
        TextView windView  = (TextView) getView().findViewById(R.id.detail_wind_textview);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        TextView pressureView  = (TextView) getView().findViewById(R.id.detail_pressure_textview);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        TurbineView turbine = (TurbineView) getView().findViewById(R.id.turbine);
        turbine.setDegrees(data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES)));
        turbine.setSpeed(data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)));

        // We still need this for the share intent
        mForecastStr = String.format("%s - %s - %s/%s", dateText, condition, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { 
    }

    public void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Log.v(TAG, "onLocationChanged: " + newLocation);
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            Log.v(TAG, "mUri: " + mUri);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            Log.v(TAG, "loader restarted");
        }
        Log.v(TAG, "onLocationChanged ended");
    }
}