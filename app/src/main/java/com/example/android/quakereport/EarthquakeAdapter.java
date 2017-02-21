package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mick Jagger on 2/18/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<SeismicData> {

    private static final String LOCATION_SEPARATOR = " of ";
    private String mUrl;
    /**
     * Custom object to hold all seismic data
     *
     * @param context       context from caller activity
     * @param objects       objects containing Seismic data
     */
    public EarthquakeAdapter(Context context, ArrayList<SeismicData> objects) {
        super(context, 0, objects);
    }

    /**
     *
     * @param position      position within ArrayList
     * @param convertView   Current view that may contain data
     * @param parent        See documentation
     * @return              View for entire Screen containing list views and prepopulate list items
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        SeismicData seismicData = getItem(position);

        //format Magnitude
        DecimalFormat formatter = new DecimalFormat("0.0");
        String formatMag = formatter.format(seismicData.getMag());
        //add new formatted Mag to TextView
        TextView magView = (TextView) listItemView.findViewById(R.id.mag_list_item);
        magView.setText(formatMag);

        //get background of magnitude
        GradientDrawable magnitudeCircle = (GradientDrawable) magView.getBackground();
        //get color based on seismic data
        int magnitudeColor = getMagnitudeColor(seismicData.getMag());
        //set background color
        magnitudeCircle.setColor(magnitudeColor);

        //get place and split into two variables
        String place = seismicData.getPlace();
        String distance;
        String city;
        if(place.contains(LOCATION_SEPARATOR)){
            String[] parts = place.split(LOCATION_SEPARATOR);
            distance = parts[0] + LOCATION_SEPARATOR;
            city = parts[1];
        } else{
            city = place;
            distance = "Near the";
        }
        //add distance to TextView
        TextView distanceView = (TextView) listItemView.findViewById(R.id.distance_list_item);
        distanceView.setText(distance);
        //add city to TextView
        TextView cityView = (TextView) listItemView.findViewById(R.id.city_list_item);
        cityView.setText(city);

        //format time into two variables
        long originalTime = seismicData.getTime();
        Date dateObject = new Date(originalTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String dateToDisplay = dateFormat.format(dateObject);
        String timeToDisplay = timeFormat.format(dateObject);

        TextView dateView = (TextView) listItemView.findViewById(R.id.date_list_item);
        dateView.setText(dateToDisplay);

        TextView timeView = (TextView) listItemView.findViewById(R.id.time_list_item);
        timeView.setText(timeToDisplay);

        //setup String for URL
        mUrl= seismicData.getUrl();
        //setup onclick listener for each ListView
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mUrl));
                getContext().startActivity(i);
            }
        });

        return listItemView;
    }

    private int getMagnitudeColor(double mag){
        int color = (int) Math.round(mag);
        switch (color){
            case 0:
            case 1:
                color =  R.color.magnitude1;
                break;
            case 2:
                color = R.color.magnitude2;
                break;
            case 3:
                color = R.color.magnitude3;
                break;
            case 4:
                color = R.color.magnitude4;
                break;
            case 5:
                color = R.color.magnitude5;
                break;
            case 6:
                color = R.color.magnitude6;
                break;
            case 7:
                color = R.color.magnitude7;
                break;
            case 8:
                color = R.color.magnitude8;
                break;
            case 9:
                color = R.color.magnitude9;
                break;
            case 10:
                color = R.color.magnitude10plus;
                break;
            default:
                break;
        }
        return ContextCompat.getColor(getContext(),color);
    }
}
