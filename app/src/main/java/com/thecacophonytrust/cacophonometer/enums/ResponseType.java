package com.thecacophonytrust.cacophonometer.enums;

import android.util.Log;

public enum ResponseType {
	
	
	//General 
	SUCCESS,
	FAIL,
	NO_RESPONSE,
	ERROR_WITH_CONNECTING_TO_SERVER,
	ERROR,
	
	//User Register
	INVALID_EMAIL,
	INVALID_PASSWORD,
	INVALID_USERNAME,
	USERNAME_IN_USE,
	EMAIL_IN_USE,
	
	//Device Register
	
	//Other
	UNRECOGNISED;
	
	private static final String LOG_TAG = "ResponseType.java";
	
	
	public static ResponseType fromString(String value){
		ResponseType key = UNRECOGNISED;
		try {
			key = valueOf(value);
		} catch (Exception e){
			Log.e(LOG_TAG, "\""+value+"\"");
			Log.e(LOG_TAG, e.toString());
			return UNRECOGNISED;
		}
		return key;
	}
}
