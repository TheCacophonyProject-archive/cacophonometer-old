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
import com.thecacophonytrust.cacophonometer.activity.SettingsActivity;

public class GPS implements LocationListener {

    private static final String LOG_TAG = "GPSLocation.java";

    static LocationManager locationManager;
    static SettingsActivity settingsActivity;
    static Context context = null;

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d(LOG_TAG, "GPS status changed.");
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double alt = location.getAltitude();
        float acc = location.getAccuracy();
        String str = "Latitude: "+lat+" Longitude: "+lon;

        Settings.getLocation().setLatitude(lat);
        Settings.getLocation().setLongitude(lon);

        Settings.getLocation().setAccuracy(acc);
        Settings.getLocation().setGPSLocationTime(location.getTime());

        if (location.hasAltitude())
            Settings.getLocation().setAltitude(alt);

        Log.d(LOG_TAG, "GPS location changed to " + str);
        if (settingsActivity != null) settingsActivity.updateLocationTextField();
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
     * Requests a GPS location update of a high accuracy.
     * @param settingsActivity settingsActivity that text is to be updated on completion of getting the location
     */
    public void update(SettingsActivity settingsActivity) {
        GPS.settingsActivity = settingsActivity;
        Log.d(LOG_TAG, "Starting GPS test");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_MEDIUM);    //TODO, see radius, pros and cons of different accuracy options.
        Log.d(LOG_TAG, c.toString() + " : " + c.getAccuracy());
        locationManager.requestSingleUpdate(c, this, null);

        Log.d(LOG_TAG, "sent GPS request");
    }

    public static void init(Context context){
        GPS.context = context;
    }
}