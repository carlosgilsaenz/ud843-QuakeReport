package com.example.android.quakereport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mick Jagger on 2/18/2017.
 */

//Creates EarthQuake object for needed for list item views
public class SeismicData {
    private Double mMag;
    private String mPlace;
    private long mTime;
    private String mUrl;

    /**
     * @param mag       input Magnitude
     * @param place      input place for distance and location
     * @param time      input time
     */
    public SeismicData(double mag, String place, long time, String url){
        mMag = mag;
        mPlace = place;
        mTime = time;
        mUrl = url;
    }

    /**
     * @return      Magnitude for given item
     */
    public double getMag(){
        return mMag;
    }

    /**
     * @return      time for given item
     */
    public long getTime(){return mTime;}

    /**
     *@return       distance for given item
     */
    public String getPlace(){return mPlace;}

    /**
     * @return      returns the URL for item
     */
    public String getUrl(){return mUrl;}
}
