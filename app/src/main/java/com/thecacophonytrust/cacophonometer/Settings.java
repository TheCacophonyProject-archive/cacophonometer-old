package com.thecacophonytrust.cacophonometer;

import java.io.File;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.util.JSONFile;
import com.thecacophonytrust.cacophonometer.util.Location;

import org.json.JSONObject;

public class Settings {

    /**
     * Holds static values and setting options to be accessed through methods.
     */
	private static final String LOG_TAG = "Settings.java";

	private static String name = "Cacophonometer";
	private static Location location = new Location();
	private static String serverUrl = null;
	private static String uploadParam = null;
	private static File homeFile = null;
	private static File tempFile = null;
	private static File recordingsToUploadFolder = null;
	private static File uploadedRecordingsFolder = null;
	private static File settingsFile = null;
	private static File rulesFolder = null;
	private static long deviceId = 0;

	private static final String DEFAULT_URL = "http://192.168.0.11:3000";
	private static final String DEFAULT_UPLOAD_PARAM = "/upload";
	private static final String DEFAULT_TEMP_FOLDER = "temp";
	private static final String DEFAULT_RULES_FOLDER = "rules";
	private static final String DEFAULT_RECORDINGS_TO_UPLOAD_FOLDER = "recordingToUpload";
	private static final String DEFAULT_UPLOADED_RECORDINGS_FOLDER = "uploadedRecordings";
	private static final String DEFAULT_SETTINGS_JSON_FILE = "settings.json";

	public static File getSettingsFile(){
		if (settingsFile == null){
			settingsFile = new File(getHomeFile(), DEFAULT_SETTINGS_JSON_FILE);
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

	public static Location getLocation(){
		return location;
	}


	/**
	 *
	 * @param json file with the settings
	 */
	public static void setFromJSON(JSONObject json){
		try{
			setServerUrl((String) json.get("SERVER_URL"));
			JSONObject locationJson = (JSONObject) json.get("LOCATION");
			Location location = new Location();
			location.setFromJson(locationJson);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
		}

	}


	/**
	 * Saves the settings as a json file in the local cacophony directory
	 */
	public static void saveToFile(){
		JSONObject jo = new JSONObject();
		try{
			jo.put("SERVER_URL", getServerUrl());
			jo.put("LOCATION", getLocation().asJSONObject());
		} catch (Exception e){
			Log.e(LOG_TAG, e.toString());
		}
		JSONFile.saveJSONObject(getSettingsFile().getAbsolutePath(), jo);
		Toast.makeText(MainActivity.getCurrent().getBaseContext(), "Settings saved", Toast.LENGTH_SHORT).show();
	}
}
