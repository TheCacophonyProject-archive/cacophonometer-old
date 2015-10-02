package com.thecacophonytrust.cacophonometer.util;

import java.io.File;
import java.io.FileFilter;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.recording.RecordingArray;
import com.thecacophonytrust.cacophonometer.recording.RecordingDataObject;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.enums.FileFilterType;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;

public class LoadData {

	/**
	 * This class loads the data from the text files and makes rules and RecordingDataObjects from them.
	 */
	
	private static final String LOG_TAG = "LoadData.java";

	/**
	 * Loads the data from the settings JSON file.
	 */
	public static void loadSettings(){
		File settingFile = Settings.getSettingsFile();
		if (!settingFile.isFile()) {
			Log.d(LOG_TAG, "Settings file was not found at: " + settingFile.getAbsolutePath());
		} else {
			Settings.setFromJSON(JSONFile.getJSON(settingFile.getAbsolutePath()));
		}
	}

	/**
	 * Loads the recordings.
	 * Gets all the JSON files in the recordings folder and uses them to make new Recording Data Objects.
	 */
	public static void loadRecordings() {
		Log.i(LOG_TAG, "Loading recording.....");
		File recordingJSONFiles[] = Settings.getRecordingsFolder().listFiles(fileFilter(FileFilterType.JSON));
		if (recordingJSONFiles == null){
			Log.i(LOG_TAG, "No recordings found.");
			return;
		}
		int recordingCount = 0;
		for (File f : recordingJSONFiles) {
			RecordingDataObject rdo = new RecordingDataObject(JSONFile.getJSON(f.toString()));	//Makes new RDO using JSON file
			Log.v(LOG_TAG, "New RDO: " + rdo.toString());

			if (rdo.isValidRecording()) {
				if (rdo.isUploaded())
					RecordingArray.addUploadedRecording(rdo);
				else
					RecordingArray.addToUpload(rdo);
				recordingCount += 1;
			}
		}
		Log.i(LOG_TAG, "Finished loading recording, "+recordingCount+" found.");
	}


	/**
	 * Goes through the folder that contains the rules .txt files.
	 * For each text file it parses it and saves a new rule then puts the rule in the RulesArray.class
	 */
	public static void loadRules() {
		Log.i(LOG_TAG, "Loading rules.....");
		File rules[] = Settings.getRulesFolder().listFiles(fileFilter(FileFilterType.JSON));
		if (rules == null) {
			Log.i(LOG_TAG, "No rules found.");
			return;
		}
		Rule r;
		for (File f : rules) {
			r = new Rule(JSONFile.getJSON(f.toString()));
			if (r.isValid())
				RulesArray.addRule(r);
			else 
				Log.d(LOG_TAG, "Invalid rule found at '"+f.toString()+"'");
		}
		Log.i(LOG_TAG, "Finished loading rules.");
		RulesArray.printRules();
	}

	/**
	 * Returns a FileFilter that filters out file that are not of the set file type.
	 * @param type of file wanted.
	 * @return FileFilter depending on what the type was.
	 */
	private static FileFilter fileFilter(FileFilterType type) {
		switch (type) {
		case FOLDERS:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
		case RECORDING_3GP:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().endsWith(".3gp")) && !pathname.isDirectory();
				}
			};
		case TEXT:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().endsWith(".txt")) && !pathname.isDirectory();
				}
			};
		case JSON:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return ((pathname.getName().endsWith(".JSONMetadata")) && !pathname.isDirectory());
				}
			};
		default:
			Log.e(LOG_TAG, "File filter of type has not been set, type: " + type);
			return null;
		}
	}
}
