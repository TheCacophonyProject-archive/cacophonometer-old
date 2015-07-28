package com.thecacophonytrust.cacophonometer.recording;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class RecordingArray {

	/**
	 * This class holds static arrays of RecordingDataObjects depending on what state they are in (need to be uploaded, uploading, uploaded).
	 */
	
	private static final String LOG_TAG = "RecordingArray.java";
	private static final long SEED = 123;
	
	private static Random random = new Random(SEED);
	
	private static List<RecordingDataObject> recordingsToUpload = new ArrayList<>();	//List of recordings that have not been uploaded.
	private static List<RecordingDataObject> uploadedRecordings = new ArrayList<>();	//List of recordings that have been uploaded.
	private static List<RecordingDataObject> uploadingRecordings = new ArrayList<>();	//List of recordings that are uploading at the moment.

	/**
	 * Adds a RecordingDataObject to the list that need to be uploaded.
	 * @param rdo RecordingDataObject
	 */
	public static void addToUpload(RecordingDataObject rdo) {
		Log.d(LOG_TAG, "Adding recording to be uploaded: " + rdo);
		recordingsToUpload.add(rdo);
	}

	/**
	 * Removes a RecordingDataObject from the toUpload array.
	 * @param rdo RecordingDataObject to remove.
	 */
	public static void removeRecordingToUpload(RecordingDataObject rdo){
		recordingsToUpload.remove(rdo);
	}

	/**
	 * Adds a RecordingDataObject to the array of uploaded recordings
	 * @param rdo RecordingDataObject to add.
	 */
	public static void addUploadedRecording(RecordingDataObject rdo){
		if (!uploadedRecordings.contains(rdo)){
			uploadedRecordings.add(rdo);
		}
	}

	/**
	 * Gets a list of recordings that are in the toUpload array that have the given rule name.
	 * @param ruleName of recordings to return
	 * @return list of recordings that have the given rule name.
	 */
	public static List<RecordingDataObject> getRecordingsToUploadByRule(String ruleName) {
		List<RecordingDataObject> rdoList = new ArrayList<>();
		for (RecordingDataObject rdo : recordingsToUpload){
			if (rdo.getRuleName().equals(ruleName))
				rdoList.add(rdo);
		}
		Log.d(LOG_TAG, "Recordings to upload by Rule '"+ruleName+"': " + Arrays.toString(rdoList.toArray()));
		return rdoList;
	}

	/**
	 * Gets a list of recordings that are in the uploaded array that have the given rule name.
	 * @param ruleName	of recordings to return.
	 * @return	list of recordings that have the given rule name.
	 */
	public static List<RecordingDataObject> getUploadedRecordingsByRule(String ruleName) {
		List<RecordingDataObject> rdoList = new ArrayList<>();
		for (RecordingDataObject rdo : uploadedRecordings){
			if (rdo.getRuleName().equals(ruleName))
				rdoList.add(rdo);
		}
		Log.d(LOG_TAG, "Recordings to upload by Rule '"+ruleName+"': " + Arrays.toString(rdoList.toArray()));
		return rdoList;
	}

	/**
	 * Gets a random RecordingDataObject from the toUpload array.
	 * @return random RecordingDataObject that isn't uploaded.
	 */
	public static RecordingDataObject getRandomToUpload(){
		if (recordingsToUpload.size() == 0)
			return null;
		return recordingsToUpload.get(random.nextInt(recordingsToUpload.size()));
	}

	/**
	 * Add a RecordingDataObject to the array of RDOs that are uploading.
	 * @param rdo uploading RecordingDataObject
	 */
	public static void addUploadingRecording(RecordingDataObject rdo){
		uploadingRecordings.add(rdo);
	}

	/**
	 * Removes a RecordingDataObject from the array of RDOs that are uploading.
	 * @param rdo RecordingDataObject that isn't uploading.
	 */
	public static void removeUploadingRecording(RecordingDataObject rdo){
		uploadingRecordings.remove(rdo);
	}

	/**
	 * Sets the recording arrays to new empty ones.
	 */
	public static void clear(){
		recordingsToUpload = new ArrayList<>();
		uploadedRecordings = new ArrayList<>();
		uploadingRecordings = new ArrayList<>();
	}
}
