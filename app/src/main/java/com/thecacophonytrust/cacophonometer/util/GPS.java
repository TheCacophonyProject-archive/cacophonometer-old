package com.thecacophonytrust.cacophonometer.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;

public class GPS implements LocationListener {

    private static final String LOG_TAG = "GPSLocation.java";

    static LocationManager locationManager;

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d(LOG_TAG, "GPS status changed.");
    }

    /**
     * Gets the latitude and longitude from the location and saves it to settings. Also saves the time that is was gotten.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String str = "Latitude: "+lat+" Longitude: "+lon;
        Toast.makeText(MainActivity.getCurrent().getApplicationContext(), str, Toast.LENGTH_LONG).show();
        Settings.setLatitude(lat);
        Settings.setLongitude(lon);
        Settings.setGPSLocationTime(location.getTime());
        Log.d(LOG_TAG, "GPS location changed to " + str);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG, "GPS turned off");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "GPS turned on");
    }

    /**
     * Requests a GPS location update of a medium accuracy
     */
    public void update() {
        Log.d(LOG_TAG, "Starting GPS test");
        locationManager = (LocationManager) MainActivity.getCurrent().getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_MEDIUM);    //TODO, see radius, pros and cons of different accuracy options.
        Log.d(LOG_TAG, c.toString() + " : " + c.getAccuracy());
        locationManager.requestSingleUpdate(c, this, null);

        Log.d(LOG_TAG, "sent GPS request");
    }

}