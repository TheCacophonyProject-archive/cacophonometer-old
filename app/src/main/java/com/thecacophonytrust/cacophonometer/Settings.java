package com.thecacophonytrust.cacophonometer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.enums.TextFileKeyType;
import com.thecacophonytrust.cacophonometer.util.TextFile;

public class Settings {

    /**
     * Holds static values and setting options to be accessed through methods.
     */
	private static final String LOG_TAG = "Settings.java";

	private static String name = "Cacophonometer";
	private static String location = null;
	private static String serverUrl = null;
	private static String uploadParam = null;
	private static File homeFile = null;
	private static File tempFile = null;
	private static File recordingsToUploadFolder = null;
	private static File uploadedRecordingsFolder = null;
	private static File settingsFile = null;
	private static File rulesFolder = null;
	private static long deviceId = 0;
	private static double longitude = 0;
	private static double latitude = 0;
	private static long gpsLocationTime = 0;
	
	private static final String DEFAULT_URL = "http://192.168.0.11:3000";
	private static final String DEFAULT_UPLOAD_PARAM = "/upload";
	private static final String DEFAULT_TEMP_FOLDER = "temp";
	private static final String DEFAULT_RULES_FOLDER = "rules";
	private static final String DEFAULT_RECORDINGS_TO_UPLOAD_FOLDER = "recordingToUpload";
	private static final String DEFAULT_UPLOADED_RECORDINGS_FOLDER = "uploadedRecordings";
	private static final String DEFAULT_SETTINGS_TEXT_FILE = "settings.txt";

	public static File getSettingsFile(){
		if (settingsFile == null){
			settingsFile = new File(getHomeFile(), DEFAULT_SETTINGS_TEXT_FILE);
		}
		return settingsFile;
	}

	public static void setServerUrl(String serverUrl) {
		Settings.serverUrl = serverUrl;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		Settings.name = name;
	}

	public static String getLocation() {
		return location;
	}

	public static void setLocation(String location) {
		Settings.location = location;
	}

    /**
     * Returns the home file. If it is null it is saved as the default home directory and the directory is created.
     * @return home file
     */
	public static File getHomeFile() {
		if (homeFile == null){
			homeFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cacophony");
			if (!homeFile.exists() && !homeFile.isDirectory() && !homeFile.mkdirs()){
				Log.e(LOG_TAG, "Unable to write to file");
				//TODO, exit program safely from here and display error.
			}
		}
		return homeFile;
	}

    /**
     * Returns the upload parameter for the url, default is (/upload).
     * If uploadParam is null it is set to the default.
     * @return upload parameter
     */
	public static String getUploadParam() {
		if (uploadParam != null)
			return uploadParam;
		else {
			Log.d(LOG_TAG, "No set upload parameter found. Using default.");
			return DEFAULT_UPLOAD_PARAM;
		}
	}

    /**
     * Return the server URL. If serverUrl it null it is set to the default.
     * @return server URL
     */
	public static String getServerUrl(){
		if (serverUrl != null){
			return serverUrl;
		} else {
			Log.d(LOG_TAG, "No set server URL found. Using default.");
			serverUrl = DEFAULT_URL;
			return serverUrl;
		}
	}

    /**
     * Returns the rules folder, folder where the rule text files are stored.
     * If rulesFolder is null it is set to default.
     * @return rules folder
     */
	public static File getRulesFolder(){
		if (rulesFolder == null){
			rulesFolder = new File(getHomeFile(), DEFAULT_RULES_FOLDER);
			if (!rulesFolder.exists() && !rulesFolder.isDirectory() && !rulesFolder.mkdirs()){
				Log.e(LOG_TAG, "Unable to write to file");
				//TODO, exit program safely from here and display error.
			}
		}
		return rulesFolder;
	}

    /**
     * Returns the uploaded recordings folder, folder where uploaded recordings .txt and .3gp files are saved.
     * If uploadedRecordingsFolder is null it is set to default value.
     * @return uploaded recordings folder
     */
	public static File getUploadedRecordingsFolder(){
		if (uploadedRecordingsFolder == null){
			uploadedRecordingsFolder = new File(getHomeFile(), DEFAULT_UPLOADED_RECORDINGS_FOLDER);
			if (!uploadedRecordingsFolder.exists() && !uploadedRecordingsFolder.isDirectory() && !uploadedRecordingsFolder.mkdirs()){
				Log.e(LOG_TAG, "Unable to write to file");
				//TODO, exit program safely from here and display error.
			}
		}
		return uploadedRecordingsFolder;
	}

    /**
     * Returns the recordings to upload folder, folder where uploaded recordings .txt and .3gp are saved.
     * If recordingsToUpload is null then it is ser to default value.
     * @return
     */
	public static File getRecordingsToUploadFolder(){
		if (recordingsToUploadFolder == null){
			recordingsToUploadFolder = new File(getHomeFile(), DEFAULT_RECORDINGS_TO_UPLOAD_FOLDER);
			if (!recordingsToUploadFolder.exists() && !recordingsToUploadFolder.isDirectory() && !recordingsToUploadFolder.mkdirs()){
				Log.e(LOG_TAG, "Unable to write to file");
				//TODO, exit program safely from here and display error.
			}
		}
		return recordingsToUploadFolder;
	}

    /**
     * Returns the temp file.
     * If tempFile is null then it is set to default.
     * @return temp file
     */
	public static File getTempFile() {
		if (tempFile == null){
			tempFile = new File(getHomeFile(), DEFAULT_TEMP_FOLDER);
			if (!tempFile.exists() && !tempFile.isDirectory() && !tempFile.mkdirs()){
				Log.e(LOG_TAG, "Unable to write to file for temp file");
				//TODO, exit program safely from here and display error.
			}
		}
		return tempFile;
	}

    /**
     * Returns the device ID.
     * If deviceID equals zero then it sets the device ID using the getTelephonyManagerId() method.
     * @return device ID
     */
	public static long getDeviceId(){
		//TODO, another form of ID should probably be used.
		if (deviceId == 0) {
			deviceId = Long.valueOf(MainActivity.getCurrent().getTelephonyManagerId());
			Log.d(LOG_TAG, "Setting device id using TelephonyManagerId to: \"" +deviceId+ "\"");
		}
		return deviceId;
	}

	/**
	 * Sets the longitude
	 * @param longitude
	 */
	public static void setLongitude(double longitude){
		Settings.longitude = longitude;
	}

	/**
	 * Gets the longitude
	 * @return longitude as a double
	 */
	public static double getLongitude(){
		return longitude;
	}

	/**
	 * Sets the latitude
	 * @param latitude
	 */
	public static void setLatitude(double latitude){
		Settings.latitude = latitude;
	}

	/**
	 * Returns the latitude
	 * @return latitude as a double
	 */
	public static double getLatitude(){
		return latitude;
	}

	public static void setGPSLocationTime(long gpsUTC){
		Settings.gpsLocationTime = gpsUTC;
	}

	/**
	 * Returns the time that the GPS location was taken in UTC.
	 * @return UTC time of GPS as a long
	 */
	public static long getGPSLocationTime(){
		return gpsLocationTime;
	}

	public static boolean saveToFile(){
		Map<TextFileKeyType, String> valueMap = new HashMap<>();
		valueMap.put(TextFileKeyType.SERVER_URL, getServerUrl());
		valueMap.put(TextFileKeyType.LONG, Double.toString(getLongitude()));
		valueMap.put(TextFileKeyType.LAT, Double.toString(getLatitude()));
		valueMap.put(TextFileKeyType.UTC_OF_GPS, Long.toString(getGPSLocationTime()));

		Toast.makeText(MainActivity.getCurrent().getBaseContext(), "Settings saved", Toast.LENGTH_SHORT).show();
		return TextFile.saveTextFile(valueMap, getSettingsFile().getParentFile(), getSettingsFile().getName());
	}
}
