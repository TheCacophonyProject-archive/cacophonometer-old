package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.Location;

import org.json.JSONObject;

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
	private Location location;
	private JSONObject phoneBuild;	//A JSON Object containing data about the phone build.
	private JSONObject buildVersion; //JSON Object containing data about the build version.


	/**
	 * Recording Data Object (RDO), this holds teh relevant data of a recording.
	 * @param ruleName of rule that started recording.
	 */
	public RecordingDataObject(String ruleName){
		this.ruleName = ruleName;
		Log.d(LOG_TAG, "Recording '" + ruleName + "' added");
	}

	/**
	 * Makes a new Recording Data Object using a JSON object.
	 * @param jsonObject to load data from
	 */
	public RecordingDataObject(JSONObject jsonObject){
		setFromJSON(jsonObject);
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
	 * Returns the file name in the form of "deviceId_utc.json"
	 * @return text file name.
	 */
	public String getJSONFileName(){
		return deviceId + "_" + utc +".json";
	}

	/**
	 * Returns the file name in the form of "deviceId_utc.3gp"
	 * @return recording file name.
	 */
	public String getRecordingFileName(){
		return deviceId + "_" + utc +".3gp";
	}

	/**
	 * Gets the recording file to return
	 * @return the recording file
	 */
	public File getRecordingFile() {
		return new File(Settings.getRecordingsFolder(), getRecordingFileName());
	}

	/**
	 * Returns a string of the file path to the recording.
	 * @return  recording file path.
	 */
	public String getRecordingFilePath(){
			return Settings.getRecordingsFolder() + "/" + getRecordingFileName();
	}

	public JSONObject asJSONObject(){
		JSONObject jo = new JSONObject();
		JSONObject rdoJSONObject = new JSONObject();
		try{
			jo.put("DEVICE_ID", Long.toString(getDeviceId()));
			jo.put("RECORDING_FIELDS", fieldsAsJSON());
			jo.put("LOCATION", getLocation().asJSONObject());
			jo.put("PHONE_BUILD", getPhoneBuild());
			jo.put("BUILD_VERSION", getBuildVersion());
			rdoJSONObject.put("DATA_POINT", jo);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error with making JSON object with RDO fields.");
			Log.e(LOG_TAG, e.getMessage());
		}
		return rdoJSONObject;
	}

	public JSONObject getPhoneBuild(){
		return phoneBuild;
	}

	public void setPhoneBuild(JSONObject phoneBuild){
		this.phoneBuild = phoneBuild;
	}

	public JSONObject getBuildVersion(){
		return buildVersion;
	}

	public void setBuildVersion(JSONObject buildVersion){
		this.buildVersion = buildVersion;
	}

	/**
	 * Makes a JSON object using the fields in the recording data object.
	 * @return JSON object of RDO
	 */
	private JSONObject fieldsAsJSON(){
		JSONObject recordingFields = new JSONObject();
		try {
			recordingFields.put("UTC", Long.toString(getUTC()));
			recordingFields.put("DURATION", Integer.toString(getDuration()));
			recordingFields.put("RULE_NAME", getRuleName());
			recordingFields.put("UPLOADED", Boolean.toString(isUploaded()));
		} catch (Exception e){
			Log.e(LOG_TAG, "Error with making JSON object with RDO fields.");
			Log.e(LOG_TAG, e.getMessage());
		}
		return recordingFields;
	}

	/**
	 * Sets variables of the recording data object using a JSON file.
	 * @param json object that contains the recording data.
	 */
	public void setFromJSON(JSONObject json){
		try{
			JSONObject jo = (JSONObject) json.get("DATA_POINT");
			//setDeviceId(Long.valueOf((String) jo.get("DEVICE_ID")));
			JSONObject recordingFieldsJSON = (JSONObject) jo.get("RECORDING_FIELDS");
			setUtc(Long.valueOf((String) recordingFieldsJSON.get("UTC")));
			setDuration(Integer.valueOf((String) recordingFieldsJSON.get("DURATION")));
			setRuleName((String) recordingFieldsJSON.get("RULE_NAME"));
			setUploaded(Boolean.valueOf((String) recordingFieldsJSON.get("UPLOADED")));

			Location location = new Location();
			JSONObject locationJSON = (JSONObject) jo.get("LOCATION");
			location.setFromJson(locationJSON);

			setPhoneBuild((JSONObject) jo.get("PHONE_BUILD"));
			setBuildVersion((JSONObject) jo.get("BUILD_VERSION"));
			Log.d(LOG_TAG, "Loaded recording from JSON: " + this.toString());

		} catch (Exception e){
			Log.v(LOG_TAG, json.toString());
			Log.e(LOG_TAG, "Exception when loading recording data object from JSON");
			Log.e(LOG_TAG, e.toString());
		}
	}
	
	@Override
	public String toString() {
		return "RecordingDataObject [deviceId=" + deviceId + ", utc=" + utc
				+ ", ruleName=" + ruleName + ", uploaded=" + uploaded
				+ ", duration=" + getDuration() + "]";
	}
}