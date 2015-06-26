package com.thecacophonytrust.cacophonometer.enums;

import android.util.Log;

public enum TextFileKeyType {
	NAME,
	START_TIME_MINUTE,
	START_TIME_HOUR,
	DURATION,
	PRIORITY,
	REPEAT,
	REPEAT_INTERVAL,
	DEVICE_ID, 
	OS_VERSION_NUMBER,
	BUILD_MODEL,
	USER_ID,
	UTC_TIME,
	TIMEZONE,
	LAT,
	LONG,
	ALT,
	EXT_MIC,
	UNRECOGNISED,
	RULE,
	UTC_OF_GPS;
	
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
