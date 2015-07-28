package com.thecacophonytrust.cacophonometer.util;

import android.util.Log;

import org.json.JSONObject;

public class Location{

    private static final String LOG_TAG = "Location.java";

    private double longitude = 0;
    private double latitude = 0;
    private long gpsLocationTime = 0;
    private float accuracy = -1;
    private double altitude = -1;
    private boolean hasAltitude = false;
    private String userLocationInput = "";

    public Location(){
        Log.d(LOG_TAG, "New location object was made");
    }

    public Location copy(){
        Location l = new Location();
        l.longitude = this.longitude;
        l.latitude = this.latitude;
        l.gpsLocationTime = this.gpsLocationTime;
        l.accuracy = this.accuracy;
        l.altitude = this.altitude;
        l.hasAltitude = this.hasAltitude;
        l.userLocationInput = this.userLocationInput;
        return l;
    }

    public void setLongitude(double longitude){
        //TODO check iv valid longitude
        this.longitude = longitude;
        Log.v(LOG_TAG, "Longitude set to " + longitude);
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLatitude(double latitude){
        //TODO check if this is a valid latitude
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setGPSLocationTime(long gpsLocationTime) {
        this.gpsLocationTime = gpsLocationTime;
    }

    public long getGPSLocationTime(){
        return gpsLocationTime;
    }

    public void setAccuracy(float accuracy){
        this.accuracy = accuracy;
    }

    public  float getAccuracy(){
        return accuracy;
    }

    public void setAltitude(double altitude){
        //TODO check if altitude is valid
        this.altitude = altitude;
        hasAltitude = true;
    }

    public double getAltitude(){
        return altitude;
    }

    public boolean hasAltitude(){
        return hasAltitude;
    }

    public void setHasAltitude(boolean hasAltitude){
        this.hasAltitude = hasAltitude;
    }

    public void setUserLocationInput(String userLocationInput){
        this.userLocationInput = userLocationInput;
    }

    public String getUserLocationInput(){
        return userLocationInput;
    }

    public void setFromJson(JSONObject locationJSON){
        try{
            setLongitude(Double.valueOf((String) locationJSON.get("LONGITUDE")));
            setLatitude(Double.valueOf((String) locationJSON.get("LATITUDE")));
            setGPSLocationTime(Long.valueOf((String) locationJSON.get("GPS_LOCATION_TIME")));
            setAccuracy(Float.valueOf((String) locationJSON.get("ACCURACY")));
            setAltitude(Double.valueOf((String) locationJSON.get("ACCURACY")));
            setHasAltitude(Boolean.valueOf((String) locationJSON.get("HAS_ALTITUDE")));
            setUserLocationInput((String) locationJSON.get("USER_LOCATION_INPUT"));
        } catch (Exception e){
            Log.e(LOG_TAG, "Error with loading location from JSON");
            Log.e(LOG_TAG, e.toString());
        }
    }

    public JSONObject asJSONObject(){
        JSONObject locationJSON = new JSONObject();
        try{
            locationJSON.put("LONGITUDE", Double.toString(getLongitude()));
            locationJSON.put("LATITUDE", Double.toString(getLatitude()));
            locationJSON.put("GPS_LOCATION_TIME", Long.toString(getGPSLocationTime()));
            locationJSON.put("ACCURACY", Float.toString(getAccuracy()));
            locationJSON.put("ALTITUDE", Double.toString(getAltitude()));
            locationJSON.put("HAS_ALTITUDE", Boolean.toString(hasAltitude()));
            locationJSON.put("USER_LOCATION_INPUT", getUserLocationInput());
        } catch (Exception e){
            Log.e(LOG_TAG, "Error with making JSON object with RDO fields.");
            Log.e(LOG_TAG, e.getMessage());
        }
        return locationJSON;
    }
}
