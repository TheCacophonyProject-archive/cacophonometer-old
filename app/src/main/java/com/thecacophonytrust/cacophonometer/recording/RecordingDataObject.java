package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.Location;

public class RecordingDataObject {

	/**
	 * This class holds the relevant data for a recording, deviceId, UTC...
	 */

	private static final String LOG_TAG = "RecordingDataObjec.java";	//LOG_TAG for android.util.Log can only be 23 characters, so convention was not followed for this class.
	
	private long deviceId;			//The device id at time of recording.
	private long utc;				//UTC time of start of recording.
	private int duration;			//Duration is seconds of the recording.
	private String timezone;		//Timezone at the place of recording.
	private String ruleName;		//Name of rule that started this recording.
	private boolean uploaded;		//True if recording has been uploaded, false if not.
	private File containingFolder;	//This is where the text file and should be found.
	private Location location;

	/**
	 * Recording Data Object (RDO), this holds teh relevant data of a recording.
	 * @param ruleName of rule that started recording.
	 */
	public RecordingDataObject(String ruleName){
		this.ruleName = ruleName;
		Log.d(LOG_TAG, "Recording '"+ruleName+"' added");
	}


	/**
	 * Gets the folder that contains the text file and recording.
	 * @return containing folder
	 */
	public File getContainingFolder() {
		if (containingFolder == null) {
			if (uploaded){
				containingFolder = Settings.getUploadedRecordingsFolder();
			} else {
				containingFolder = Settings.getRecordingsToUploadFolder();
			}
		}
		return containingFolder;
	}

	/**
	 * Sets the containing folder of the data text file and recording.
	 * @param containingFolder
	 */
	public void setContainingFolder(File containingFolder) {
		this.containingFolder = containingFolder;
	}

	/**
	 * Sets the name of the rule that started the recording.
	 * @param ruleName
	 */
	public void setRuleName(String ruleName){
		this.ruleName = ruleName;
	}

	/**
	 * Gets the cacophonommeter device ID that was used for the recording.
	 * @return device ID
	 */
	public long getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets the cacophonometer device ID that was used for the recording.
	 * @param deviceId
	 */
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Gets the UTC time for the start of the recording.
	 * @return UTC time at start of recording.
	 */
	public long getUTC() {
		return utc;
	}

	/**
	 * Sets the UTC time for the start of the recording.
	 * @param utc at start of recording.
	 */
	public void setUtc(long utc) {
		this.utc = utc;
	}

	/**
	 * Gets the duration of the recording in seconds
	 * @return duration in seconds.
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the recording in seconds.
	 * @param duration in seconds.
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Gets the time zone that was active at time of recording.
	 * @return
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * Sets the time zone that was active at time of recording.
	 * @param timezone
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		if (location == null) {
			Log.v(LOG_TAG, "Making Location object for a RDO.");
			location = new Location();
		}
		return location;
	}

	/**
	 * Returns the rule name that started this recording.
	 * @return	the rule name
	 */
	public String getRuleName() {
		return ruleName;
	}

	/**
	 * Returns if the device was uploaded or not as a boolean.
	 * @return if recording is uploaded.
	 */
	public boolean isUploaded() {
		return uploaded;
	}

	/**
	 * Sets if the recording was uploaded or not.
	 * @param uploaded
	 */
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	/**
	 * Checks if the recording is valid and returns result as a boolean.
	 * @return if recording is valid.
	 */
	public boolean isValidRecording() {
		boolean result = true;
		//TODO actually check if it is valid.
		return result;
	}

	/**
	 * Returns the file name in the form of "deviceId_utc.txt"
	 * @return text file name.
	 */
	public String getTextFileName(){
		String fileName = deviceId + "_" + utc +".txt";
		return fileName;
	}

	/**
	 * Returns the file name in the form of "deviceId_utc.3gp"
	 * @return recording file name.
	 */
	public String getRecordingFileName(){
		String fileName = deviceId + "_" + utc +".3gp";
		return fileName;
	}

	/**
	 * Gets the recording file to return
	 * @return the recording file
	 */
	public File getRecordingFile() {
			return new File(getContainingFolder(), getRecordingFileName());
	}

	/**
	 * Returns a string of the file path to the recording.
	 * @return  recording file path.
	 */
	public String getRecordingFilePath(){
			return getContainingFolder() + "/" + getRecordingFileName();
	}
	
	@Override
	public String toString() {
		return "RecordingDataObject [deviceId=" + deviceId + ", utc=" + utc
				+ ", ruleName=" + ruleName + ", uploaded=" + uploaded
				+ ", duration=" + getDuration() + "]";
	}
}