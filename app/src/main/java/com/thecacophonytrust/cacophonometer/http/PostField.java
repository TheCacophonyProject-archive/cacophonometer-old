package com.thecacophonytrust.cacophonometer.http;

import com.thecacophonytrust.cacophonometer.enums.HttpPostFieldType;

public class PostField {
	private HttpPostFieldType key = null;
	private String value = null;
	private int isValid = -1;			//-1: not checked, 0: not valid, 1: valid
	
	public PostField(HttpPostFieldType key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public HttpPostFieldType getKey(){
		return key;
	}
	
	public boolean isValid(){
		if (isValid == -1) checkIfValid();
		if (isValid == 1) return true;
		else return false;
	}

	private void checkIfValid(){
		switch (key) {
		case USER_ID:
			isValidUserId();
			break;
		case DEVICE_ID:
			isValidDeviceId();
			break;
		case UTC:
			isValidUTC();
			break;
		case LAT:
			isValidLat();
			break;
		case LONG:
			isValidLong();
			break;
		case ALT:
			isValidAlt();
			break;
		case DURATION:
			isValidDuration();
			break;
		case TIME_ZONE:
			isValidTimeZone();
			break;
		case BITRATE:
			isValidBitrate();
			break;
		case EXT_MIC:
			isValidExtMic();
			break;
		default:
			break;
		}
	}
	
	private void isValidExtMic() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidBitrate() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidDuration() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidTimeZone() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidAlt() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidLong() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidLat() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidUTC() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidDeviceId() {
		// TODO Auto-generated method stub
		isValid = 1;
	}

	private void isValidUserId() {
		// TODO Auto-generated method stub
		isValid = 1;
	}
}
