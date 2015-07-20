package com.thecacophonytrust.cacophonometer.util;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.recording.RecordingArray;
import com.thecacophonytrust.cacophonometer.recording.RecordingDataObject;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.enums.FileFilterType;
import com.thecacophonytrust.cacophonometer.enums.TextFileKeyType;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;

public class LoadData {

	/**
	 * This class loads the data from the text files and makes rules and RecordingDataObjects from them.
	 */
	
	private static final String LOG_TAG = "LoadData.java";

	/**
	 * Loads the data from the settings text file
	 */
	public static void loadSettings(){
		File settingFile = Settings.getSettingsFile();
		if (!settingFile.isFile()) {
			Log.d(LOG_TAG, "Settings file was not found at: " + settingFile.getAbsolutePath());
		} else {
			Settings.setFromJSON(JSONFile.getJSON(settingFile.getAbsolutePath()));
		}
	}

	private static void parseValuePairForSettings(Map<TextFileKeyType, String> dataMap){
		for (TextFileKeyType key : dataMap.keySet()){
			switch (key){
				case SERVER_URL:
					try{
						URL url = new URL(dataMap.get(key));	//check if url is valid
						Settings.setServerUrl(dataMap.get(key));
					} catch (MalformedURLException e){
						Log.e(LOG_TAG, "Error with getting URL from settings text file");
						Log.e(LOG_TAG, e.toString());
					}
					break;
				case LATITUDE:
					try{
						Settings.getLocation().setLatitude(Double.valueOf(dataMap.get(key)));
					} catch (NumberFormatException e){
						Log.e(LOG_TAG, "Error with getting latitude from settings text file");
						Log.e(LOG_TAG, e.toString());
					}
					break;
				case LONGITUDE:
					try{
						Settings.getLocation().setLongitude(Double.valueOf(dataMap.get(key)));
					} catch (NumberFormatException e){
						Log.e(LOG_TAG, "Error with getting longitude from settings text file");
						Log.e(LOG_TAG, e.toString());
					}

					break;
				case UTC_OF_GPS:
					try {
						Settings.getLocation().setGPSLocationTime(Long.valueOf(dataMap.get(key)));
					} catch (NumberFormatException e){
						Log.e(LOG_TAG, "Error with getting UTC_OF_GPS from settings text file");
						Log.e(LOG_TAG, e.toString());
					}
					break;
				default:
					//TODO add other cases...
					break;
			}
		}
	}

	public static void loadRecordings() {
		Log.i(LOG_TAG, "Loading recording.....");
		File recordingTextFiles[] = Settings.getRecordingsToUploadFolder().listFiles(fileFilter(FileFilterType.JSON));
		int recordingCount = 0;
		for (int i = 0; i < recordingTextFiles.length; i++) {
			RecordingDataObject rdo = loadRecording(recordingTextFiles[i]);
			if (rdo.isValidRecording()) {
				RecordingArray.addToUpload(rdo);
				recordingCount += 1;
			}
		}
		Log.i(LOG_TAG, "Finished loading recordings to upload, "+recordingCount+" found.");
	}

	/**
	 * Goes through the uploaded recordings folder and finds the .txt and .3gp files of recordings 
	 * and saves the data to a RecordingDataObject. Then puts teh RDO in the uploaded array in RecordingArray.class
	 */
	public static void loadUploadedRecordings() {
		Log.i(LOG_TAG, "Loading uploaded recordings.....");
		File recordingTextFiles[] = Settings.getUploadedRecordingsFolder().listFiles(fileFilter(FileFilterType.TEXT));
		int recordingCount = 0;
		for (int i = 0; i < recordingTextFiles.length; i++) {
			RecordingDataObject rdo = loadRecording(recordingTextFiles[i]);
			rdo.setUploaded(true);
			if (rdo.isValidRecording()) {
				RecordingArray.addUploadedRecording(rdo);
				recordingCount += 1;
			} else {
				Log.e(LOG_TAG, "Found invalid recording.");
			}
		}
		Log.i(LOG_TAG, "Finished loading uploaded recordings, "+recordingCount+" found.");
	}

	/**
	 * Takes text file of a recordings data and returns a RecordingDataObject that has the respective values from the text file.
	 * @param textRecordingFile to parse.
	 * @return RecordingDataObject with appropriate values from the text file.
	 */
	private static RecordingDataObject loadRecording(File textRecordingFile) {
		RecordingDataObject rdo = new RecordingDataObject("tempName");
		Map<TextFileKeyType, String> dataMap = TextFile.parseTextFile(textRecordingFile); 
		Log.d(LOG_TAG, "DataMap: " + dataMap);
		for (TextFileKeyType key : dataMap.keySet()){
			parseValuePairForRecording(key, dataMap.get(key), rdo);
		}
		return rdo;
	}

	/**
	 * Takes a key and string and uses the key to see what the string represents.
	 * Then parses and saves the value to the recordingDataObject.
	 * @param key value from line in text file.
	 * @param value value from line in text file.
	 * @param rdo RecordingDataObject to save value to.
	 */
	private static void parseValuePairForRecording(TextFileKeyType key, String value, RecordingDataObject rdo) {
		Log.v(LOG_TAG, "Parsing value pair, Key: "+key+", Value: "+value);
		switch (key) {
		case DEVICE_ID:
			try {
				rdo.setDeviceId(Long.parseLong(value));
			} catch (NumberFormatException e) {
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
			}
			break;
		case UTC_TIME:
			try {
				rdo.setUtc(Long.parseLong(value));
			} catch (NumberFormatException e) {
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
			}
			break;
		case DURATION:
			try {
				rdo.setDuration(Integer.parseInt(value));
			} catch (NumberFormatException e) {

			}
			break;
		case RULE:
			rdo.setRuleName(value);
			break;
		case LATITUDE:
			try {
				rdo.getLocation().setLatitude(Double.parseDouble(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case LONGITUDE:
			try {
				rdo.getLocation().setLatitude(Double.parseDouble(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case LONG:
			try {
				rdo.getLocation().setLatitude(Double.parseDouble(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case LAT:
			try {
				rdo.getLocation().setLatitude(Double.parseDouble(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case UTC_OF_GPS:
			try {
				rdo.getLocation().setLatitude(Long.parseLong(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case USER_LOCATION_INPUT:
			rdo.getLocation().setUserLocationInput(value);
			break;
		case ALTITUDE:
			try {
				rdo.getLocation().setAltitude(Long.parseLong(value));
			} catch (NumberFormatException e){
				Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				Log.d(LOG_TAG, e.toString());
			}
			break;
		case ALT:
			// TODO
			break;
		case EXT_MIC:
			// TODO
			break;
		case UNRECOGNISED:
			Log.d(LOG_TAG, "Unrecognised key");
			break;
		default:
			Log.d(LOG_TAG, "Unrecognised key for a recording.");
		}
	}

	/**
	 * Goes through the folder that contains the rules .txt files.
	 * For each text file it parses it and saves a new rule then puts the rule in the RulesArray.class
	 */
	public static void loadRules() {
		Log.i(LOG_TAG, "Loading rules.....");
		File rules[] = Settings.getRulesFolder().listFiles(fileFilter(FileFilterType.TEXT));
		Map<TextFileKeyType, String> dataMap;
		Rule r;
		Log.d(LOG_TAG, "Rules: " + rules);
		for (int i = 0; i < rules.length; i++) {
			dataMap = TextFile.parseTextFile(rules[i]);
			r = parseDataMapForRule(dataMap);
			if (r.isValid())
				RulesArray.addRule(r);
			else 
				Log.d(LOG_TAG, "Invalid rule found at '"+rules[i].getPath()+"'");
		}
		Log.i(LOG_TAG, "Finished loading rules.");
		RulesArray.printRules();
	}

	private static Rule parseDataMapForRule(Map<TextFileKeyType, String> dataMap) {
		Rule r = new Rule();
		for (TextFileKeyType key : dataMap.keySet()) {
			String value = dataMap.get(key);
			switch (key) {
			case NAME:
				r.setName(value);
				break;
			case START_TIME_HOUR:
				try {
					r.setStartTimeHour(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				}
				break;
			case START_TIME_MINUTE:
				try {
					r.setStartTimeMinute(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				}
				break;
			case DURATION:
				try {
					r.setDuration(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					Log.d(LOG_TAG, "Failed to parse "+key+" value: '" + value + "'");
				}
				break;
			default:
				Log.d(LOG_TAG, "Unrecognised key for a rule.");
			}
		}
		return r;
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
					if (pathname.isDirectory())
						return true;
					else
						return false;
				}
			};
		case RECORDING_3GP:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if ((pathname.getName().endsWith(".3gp")) && !pathname.isDirectory())
						return true;
					else
						return false;
				}
			};
		case TEXT:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if ((pathname.getName().endsWith(".txt")) && !pathname.isDirectory())
						return true;
					else
						return false;
				}
			};
		case JSON:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return ((pathname.getName().endsWith(".json")) && !pathname.isDirectory());
				}
			};
		default:
			Log.e(LOG_TAG, "File filter of type has not been set, type: " + type);
			return null;
		}
	}
}
