package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JSONFile;
import com.thecacophonytrust.cacophonometer.util.TextFile;

import android.media.MediaRecorder;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.enums.TextFileKeyType;

public class Recording{
	
	private static final String LOG_TAG = "Recording.java";
	private static int tempId = 0;
	static MediaRecorder recorder = null;
	private static RecordingDataObject lastRDO;
	private static String filePath;
	private static Rule rule;
	private static boolean recording = false;

	/**
	 * This class is called to stop the recording of a rule.
	 * It stop the recording and saves a new RecordingDataObject and inputs the required variables for the RDO.
	 * The RecordingDataObject is also added to the list of recordings to be uploaded in RecordingArray.class
	 */
	public static void stopRecording() {
		recorder.stop();
		recording = false;
		Log.i(LOG_TAG, "Finished recording");

		//Getting data for the RecordingDataObject
		long deviceId = Settings.getDeviceId();
		long utc = System.currentTimeMillis();
		String ruleName = rule.getName();
		int duration = rule.getDuration();

		//Making and setting values of RecordingDataObject then saving to disk.
		RecordingDataObject rdo = new RecordingDataObject(ruleName);
		rdo.setUtc(utc);
		rdo.setDeviceId(deviceId);
		rdo.setDuration(duration);
		rdo.setUploaded(false);

		rdo.setLocation(Settings.getLocation().copy());
		rdo.setBuildVersion(MainActivity.getBuildVersion());
		rdo.setPhoneBuild(MainActivity.getPhoneBuild());

		changeContainingFolder(rdo, Settings.getRecordingsToUploadFolder());
		
		//Moving recording to appropriate place
		File recordingFile = new File(filePath);
		recordingFile.renameTo(rdo.getRecordingFile());
		
		if (rdo.isValidRecording()) {
			RecordingArray.addToUpload(rdo);
			JSONFile.saveJSONObject(rdo.getContainingFolder().toString()+"/"+rdo.getJSONFileName(), rdo.asJSONObject());
			lastRDO = rdo;
		} else 
			Log.e(LOG_TAG, "Recording data object is not valid.");
	}

	/**
	 * Starts a new recording, saving the file to a temp directory.
	 * @param rule rule that started recording.
	 * @return true if recording started successfully, false if not.
	 */
	public static boolean startRecord(Rule rule) {
		Recording.rule = rule;
		tempId += 1;
		filePath = Settings.getTempFile().getPath() + "/" + tempId + ".3gp";
		Log.d(LOG_TAG, "Recording temp file: '" + filePath + "'");

		Log.d(LOG_TAG, "Settingup recording");
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setOutputFile(filePath);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			recorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "Recorder prepare failed.");
			return false;
			//TODO add some more notification to the user that the recorder isn't working.
		}
		Log.i(LOG_TAG, "Starting recording.");
		recorder.start();
		recording = true;
		
		return true;
	}

	/**
	 * This sets the folder that contains the text and .3gp (recording) file.
	 * This method is different from the RecordingDataObject.setContainingFolder
	 * method as it moves the files to the new location.
	 * @param rdo	RecordingDataObject to have its containing folder changed.
	 * @param newFolder new containing folder for the RecordingDataObject
	 */
	public static void changeContainingFolder(RecordingDataObject rdo, File newFolder) {
		File oldFolder = rdo.getContainingFolder();

		//Create new folder
		if (!newFolder.exists() && !newFolder.isDirectory() && !newFolder.mkdirs()){
			Log.e(LOG_TAG, "Unable to create dir: \""+newFolder.getPath()+"\"");
			//TODO, should exit and show error from here.
		}

		// Move text file
		File newTextFile = new File(newFolder, rdo.getJSONFileName());
		File oldTextFile = new File(oldFolder, rdo.getJSONFileName());
		Log.d(LOG_TAG, "New Text Path: '" + newTextFile.getPath() + "'");
		Log.d(LOG_TAG, "Old Text Path: '" + oldTextFile.getPath() + "'");
		if (newTextFile.exists()) {
			Log.d(LOG_TAG, "Recording text file is already in correct place.");
			// TODO check if file is valid
		} else {
			if (oldTextFile.exists()) {
				oldTextFile.renameTo(newTextFile);
				Log.d(LOG_TAG, "Moved text file to '" + newTextFile.getPath() + "'.");
			} else {
				Log.d(LOG_TAG, "Text file not found at '" + oldTextFile.getPath() + "'");
			}
		}
		// Move recording
		File newRecording = new File(newFolder, rdo.getRecordingFileName());
		File oldRecording = new File(oldFolder, rdo.getRecordingFileName());
		Log.d(LOG_TAG, "New Recording Path: '" + newRecording.getPath() + "'");
		Log.d(LOG_TAG, "Old Recording Path: '" + oldRecording.getPath() + "'");
		if (newRecording.exists()) {
			Log.d(LOG_TAG, "Recording is allready in correct place.");
			// TODO check if recording is valid
		} else {
			if (oldRecording.exists()) {
				oldRecording.renameTo(newRecording);
				Log.d(LOG_TAG, "Moved recordingfile to '" + newRecording.getPath() + "'.");
			} else {
				Log.d(LOG_TAG, "recording not found at '" + oldRecording.getPath() + "'");
			}
		}
		rdo.setContainingFolder(newFolder);
	}

	/**
	 * Returns true if device is recording and false if not.
	 * @return isRecording
	 */
	public static boolean isRecording(){
		return recording;
	}

	/**
	 * Saves the RecordingDataObject to file. Saves the UTC, DEVICE_ID, DURATION and RULE to a text file.
	 * @param rdo RecordingDataObject to be saved.
	 * @return true if save was successful, false if not.
	 */
	public static boolean saveToFile(RecordingDataObject rdo) {
		Map<TextFileKeyType, String> valueMap = new HashMap<>();

		valueMap.put(TextFileKeyType.UTC_TIME, Long.toString(rdo.getUTC()));
		valueMap.put(TextFileKeyType.DEVICE_ID, Long.toString(rdo.getDeviceId()));
		valueMap.put(TextFileKeyType.DURATION, Integer.toString(rdo.getDuration()));
		valueMap.put(TextFileKeyType.RULE, rdo.getRuleName());
		valueMap.put(TextFileKeyType.LATITUDE, Double.toString(rdo.getLocation().getLatitude()));
		valueMap.put(TextFileKeyType.LONGITUDE, Double.toString(rdo.getLocation().getLongitude()));
		valueMap.put(TextFileKeyType.UTC_OF_GPS, Long.toString(rdo.getLocation().getGPSLocationTime()));
		valueMap.put(TextFileKeyType.USER_LOCATION_INPUT, rdo.getLocation().getUserLocationInput());
		if (rdo.getLocation().hasAltitude())
			valueMap.put(TextFileKeyType.ALTITUDE, Double.toString(rdo.getLocation().getAltitude()));
		return TextFile.saveTextFile(valueMap, rdo.getContainingFolder(), rdo.getJSONFileName());
	}

	/**
	 * This returns that last recording that was recorded for this session.
	 * @return most recent RecordingDataObject
	 */
	public static RecordingDataObject getLastRecordingDataObject(){
		return lastRDO;
	}

	/**
	 * Updates the containing folder depending on if the recording is uploaded or not.
	 * @param rdo RecordingDataObject to update containing folder.
	 */
	public static void updateFileLocation(RecordingDataObject rdo){
		if (rdo.isUploaded()){
			changeContainingFolder(rdo, Settings.getUploadedRecordingsFolder());
		} else {
			changeContainingFolder(rdo, Settings.getRecordingsToUploadFolder());
		}
	}
}
