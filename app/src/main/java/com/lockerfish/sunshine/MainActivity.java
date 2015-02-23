package com.lockerfish.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lockerfish.sunshine.data.WeatherContract.WeatherEntry;

public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String TAG = getClass().getSimpleName();
    private final boolean D = Log.isLoggable(TAG, Log.DEBUG);

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) { Log.v(TAG, "onCreate: savedInstanceState: " + savedInstanceState);}

        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);
        if (D) { Log.v(TAG, "mLocation: " + mLocation);}

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (D) { Log.v(TAG, "savedInstanceState: " + savedInstanceState);}
            if (savedInstanceState == null) {


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setUseTodayLayout(!mTwoPane);

        if (D) { Log.v(TAG, "onCreate: " + mTwoPane); }
    }

    @Override
    public void onStart() {
        if (D) { Log.v(TAG, "onStart");}

        super.onStart();
    }

    @Override
    public void onResume() {
        if (D) { Log.v(TAG, "onResume");}

        super.onResume();

        String location = Utility.getPreferredLocation(this);
        if (D) { Log.v(TAG, "location: " + location); }
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (D) { Log.v(TAG, "ff: " + ff); }
            if (null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (D) { Log.v(TAG, "df: " + df); }
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onPause() {
        if (D) { Log.v(TAG, "onPause");}

        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) { Log.v(TAG, "onStop");}

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (D) { Log.v(TAG, "onDestroy");}

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (D) { Log.v(TAG, "onCreateOptionsMenu: menu: " + menu);}

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (D) { Log.v(TAG, "onOptionsItemSelected: item: " + item);}

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        if (D) { Log.v(TAG, "openPreferredLocationInMap");}

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPrefs.getString(
            getString(R.string.pref_location_key),
            getString(R.string.pref_location_default)
            );

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
            .appendQueryParameter("q", location)
            .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            if (D) { Log.d(TAG, "Couldn't call " + location + ", no receiving apps installed!"); }
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (D) { Log.v(TAG, "onItemSelected: contentUri: " + contentUri);}

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

}
