package com.thecacophonytrust.cacophonometer.util;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.activity.SettingsActivity;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.ResourcesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class GPS implements LocationListener {

    private static final String LOG_TAG = "GPSLocation.java";

    static LocationManager locationManager;
    static SettingsActivity settingsActivity;
    static Context context = null;

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Logger.d(LOG_TAG, "GPS status changed.");
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        Toast.makeText(context, "Location change", Toast.LENGTH_SHORT).show();
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        long alt = Math.round(location.getAltitude());
        int acc = Math.round(location.getAccuracy());
        String str = "Latitude: "+lat+" Longitude: "+lon;

        JSONObject jo = new JSONObject();
        try {
            jo.put("latitude", lat);
            jo.put("longitude", lon);
            jo.put("altitude", alt);
            jo.put("accuracy", acc);
            jo.put("timestamp", ResourcesUtil.iso8601.format(new Date(location.getTime())));

        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error when making a location json.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.e(LOG_TAG, sw.toString());
        }
        Location.addAndSave(jo);


        Logger.d(LOG_TAG, "GPS location changed to " + str);
        if (settingsActivity != null) settingsActivity.updateLocationTextField();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Logger.d(LOG_TAG, "GPS turned off");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Logger.d(LOG_TAG, "GPS turned on");
    }

    /**
     * Requests a GPS location update of a high accuracy.
     * @param settingsActivity settingsActivity that text is to be updated on completion of getting the location
     */
    public void update(SettingsActivity settingsActivity) {
        if (settingsActivity != null) GPS.settingsActivity = settingsActivity;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_MEDIUM);    //TODO, see radius, pros and cons of different accuracy options.
        locationManager.requestSingleUpdate(c, this, null);

        Logger.d(LOG_TAG, "Sent GPS request");
        Toast.makeText(context, "Sent GPS request", Toast.LENGTH_SHORT).show();
    }

    public static void init(Context context){
        GPS.context = context;
    }
}