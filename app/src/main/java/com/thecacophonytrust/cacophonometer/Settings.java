package com.thecacophonytrust.cacophonometer;

import java.io.File;

import android.os.Environment;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

public class Settings {

    /**
     * Holds static values and setting options to be accessed through methods.
     */
	private static final String LOG_TAG = "Settings.java";

	private static String name = "Cacophonometer";
	private static String serverUrl = null;
	private static String uploadParam = null;
	private static File homeFile = null;
	private static File tempFile = null;
	private static File recordingFolder = null;
	private static File videoRecordingFolder = null;
	private static File settingsFile = null;
	private static File rulesFolder = null;
	private static int deviceId = 0;

	private static final String DEFAULT_URL = "http://52.64.67.145:8888/";
	private static final String DEFAULT_UPLOAD_PARAM = "/upload";
	private static final String DEFAULT_TEMP_FOLDER = "temp";
	private static final String DEFAULT_RULES_FOLDER = "rules";
	private static final String DEFAULT_RECORDINGS_FOLDER = "recordings";
	private static final String DEFAULT_VIDEO_RECORDINGS_FOLDER = "videoRecordings";
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
				Logger.e(LOG_TAG, "Unable to write to file");
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
			Logger.d(LOG_TAG, "No set upload parameter found. Using default.");
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
			Logger.d(LOG_TAG, "No set server URL found. Using default.");
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
				Logger.e(LOG_TAG, "Unable to write to file");
				//TODO, exit program safely from here and display error.
			}
		}
		return rulesFolder;
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
				Logger.e(LOG_TAG, "Unable to write to file for temp file");
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
	public static int getDeviceId(){
		//TODO, another form of ID should probably be used.
		if (deviceId == 0) {
			deviceId = 1;
			Logger.d(LOG_TAG, "Setting device id using TelephonyManagerId to: \"" +deviceId+ "\"");
		}
		return deviceId;
	}

	/**
	 * Returns the folder that contains the recordings after checking that is is a current folder.
	 * @return folder that contains the recordings (JSON files and .3gp files)
	 */
	public static File getRecordingsFolder(){
		if (recordingFolder == null){
			recordingFolder = new File(getHomeFile(), DEFAULT_RECORDINGS_FOLDER);
			if (!recordingFolder.exists() && !recordingFolder.isDirectory() && !recordingFolder.mkdirs()){
				Logger.e(LOG_TAG, "Error with the recording Folder");
				//TODO try to fix problem and if cant output error message then exit, maybe send error to server.
			}
		}
		return recordingFolder;
	}

	public static File getVideoRecordingsFolder(){
		if (videoRecordingFolder == null){
			videoRecordingFolder = new File(getHomeFile(), DEFAULT_VIDEO_RECORDINGS_FOLDER);
			if (!videoRecordingFolder.exists() && !videoRecordingFolder.isDirectory() && !videoRecordingFolder.mkdirs()){
				Logger.e(LOG_TAG, "Error with the video recording Folder");
				//TODO try to fix problem and if cant output error message then exit, maybe send error to server.
			}
		}
		return recordingFolder;
	}

	/**
	 * Sets the settings using a JSON file.
	 * @param json file with the settings
	 */
	public static void setFromJSON(JSONObject json){
		try{
			setServerUrl((String) json.get("serverUrl"));
			Logger.i(LOG_TAG, "Settings were loaded from JSON");
		} catch (Exception e) {
			Logger.e(LOG_TAG, "Error with setting settings variables from json.");
			Logger.exception(LOG_TAG, e);
		}
	}


	/**
	 * Saves the settings as a json file in the local cacophony directory
	 */
	public static void saveToFileAsJSON(){
		JSONObject jo = new JSONObject();
		try{
			jo.put("serverUrl", getServerUrl());
		} catch (Exception e){
			Logger.e(LOG_TAG, "Error with making settings json object.");
			Logger.exception(LOG_TAG, e);
		}
		JsonFile.saveJSONObject(getSettingsFile().getAbsolutePath(), jo);
	}

	public static String getAppVersion(){
		return "TESTING VERSION STILL";
	}
}
