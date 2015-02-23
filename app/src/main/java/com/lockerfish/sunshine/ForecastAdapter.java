package com.lockerfish.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import com.lockerfish.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final String TAG = getClass().getSimpleName();
    private final boolean D = Log.isLoggable(TAG, Log.DEBUG);

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        if (D) { Log.v(TAG, "ForecastAdapter: context: " + context
            + " cursor: " + c 
            + " flags: " + flags);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (D) { Log.v(TAG, "getItemViewType: position " + position);}
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        if (D) { Log.v(TAG, "setUseTodayLayout: useTodayLayout: " + useTodayLayout);}
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getViewTypeCount() {
        if (D) { Log.v(TAG, "getViewTypeCount");}
        return 2;
    }

    private int getItemLayout(int viewType) {
        if (D) { Log.v(TAG, "getItemLayout: viewType: " + viewType);}

        int layoutId = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;                
            }
        }
        return layoutId;
    }

    private int getItemImageResource(int viewType, Cursor cursor) {
        if (D) { Log.v(TAG, "getItemImageResource: viewType: " + viewType 
            + " cursor: " + cursor);
        }

        int imgResource = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                imgResource = Utility.getArtResourceForWeatherCondition(
                    cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                imgResource = Utility.getIconResourceForWeatherCondition(
                    cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
            }
        }
        return imgResource;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (D) { Log.v(TAG, "newView: context: " + context 
            + " cursor: " + cursor 
            + " parent: " + parent);
        }

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = getItemLayout(viewType);

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (D) { Log.v(TAG, "bindView: view: " + view 
            + " context: " + context 
            + " cursor: " + cursor);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
 
        // Use placeholder image for now
        int viewType = getItemViewType(cursor.getPosition());
        int imgResource = getItemImageResource(viewType, cursor);
        viewHolder.iconView.setImageResource(imgResource);

        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, date));
 
        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(desc);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);
 
        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));
 
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        private final String TAG = getClass().getSimpleName();
        private final boolean D = Log.isLoggable(TAG, Log.DEBUG);
     
        public ViewHolder(View view) {
            if (D) { Log.v(TAG, "ViewHolder: view: " + view);}
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}