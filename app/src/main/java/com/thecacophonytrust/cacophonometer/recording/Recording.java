package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;
import java.io.IOException;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JSONFile;

import android.media.MediaRecorder;
import android.util.Log;

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
		long utc = System.currentTimeMillis();	//TODO this should be done at the start of the recording
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

		//Moving recording to appropriate place
		File recordingFile = new File(filePath);
		if (!recordingFile.renameTo(rdo.getRecordingFile())){
			Log.e(LOG_TAG, "Renaming file '"+recordingFile.toString()+"' to '"+rdo.getRecordingFile()+"' failed.");
			//TODO deal with this error, don't know how yet.
		}
		
		if (rdo.isValidRecording()) {
			RecordingArray.addToUpload(rdo);
			JSONFile.saveJSONObject(Settings.getRecordingsFolder()+"/"+rdo.getJSONFileName(), rdo.asJSONObject());
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
	 * Returns true if device is recording and false if not.
	 * @return isRecording
	 */
	public static boolean isRecording(){
		return recording;
	}



	/**
	 * This returns that last recording that was recorded for this session.
	 * @return most recent RecordingDataObject
	 */
	public static RecordingDataObject getLastRecordingDataObject(){
		return lastRDO;
	}
}
