package com.thecacophonytrust.cacophonometer.enums;

import android.util.Log;

public enum TextFileKeyType {
	NAME,
	START_TIME_MINUTE,	//The minute that the recording starts
	START_TIME_HOUR,	//The hour that the recording starts
	DURATION,
	PRIORITY,
	REPEAT,
	REPEAT_INTERVAL,
	DEVICE_ID, 			//The device ID
	OS_VERSION_NUMBER,
	BUILD_MODEL,
	USER_ID,
	UTC_TIME,			//UTC time of when the recording was taken
	TIMEZONE,
	LATITUDE,				//Latitude
	LONGITUDE,				//Longitude
	LONG,
	LAT,
	ALT,				//Altitude
	EXT_MIC,			//External mic
	UNRECOGNISED,		//value is unrecognised
	RULE,				//Name of the rule
	UTC_OF_GPS,			//UTC of the time that the GPS location was taken
	SERVER_URL,			//URL of the server
	HAS_ALTITUDE,
	HAS_LOCATION_ACCURACY,
	LOCATION_ACCURACY,
	ALTITUDE,
	USER_LOCATION_INPUT;
	
	private static final String LOG_TAG = "TextFileKeyType.java";
	
	public static TextFileKeyType fromString(String value){
		TextFileKeyType key = UNRECOGNISED;
		try {
			key = valueOf(value);
		} catch (Exception e){
			Log.e(LOG_TAG, e.toString());
			return UNRECOGNISED;
		}
		return key;
	}
}
