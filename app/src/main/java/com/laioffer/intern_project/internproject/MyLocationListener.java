package com.laioffer.intern_project.internproject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
/**
 * Created by weiweich on 5/18/15.
 */
public class MyLocationListener implements LocationListener
{
    private double lat = 37.3939;
    private double lon = -122.079;
    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }
    @Override
    public void onLocationChanged(Location loc)
    {
        lat = loc.getLatitude();
        lon = loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

}