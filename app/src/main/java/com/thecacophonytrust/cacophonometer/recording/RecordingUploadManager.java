package com.thecacophonytrust.cacophonometer.recording;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.http.PostDataObject;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.http.Post;
import com.thecacophonytrust.cacophonometer.http.PostField;
import com.thecacophonytrust.cacophonometer.http.PostFile;
import com.thecacophonytrust.cacophonometer.http.PostResponse;
import com.thecacophonytrust.cacophonometer.enums.HttpPostFieldType;

public class RecordingUploadManager {

	/**
	 * This class manages the uploading of the recordings.
	 */

	private static final String LOG_TAG = "Recording.java";
	
	private static final int MAX_SIM_UPLOADS = 3;
	
	private static Map<RecordingDataObject, Post> rdoMap = new HashMap<>();

	/**
	 * Starts to upload the RecordingDataObject and adds it to the list of uploading RecordingDataObjects.
	 * @param rdo	RecordingDataObject to be added.
	 * @return if recording was added successfuly
	 */
	public static boolean addRDO(RecordingDataObject rdo){
		boolean result = false;
		Log.d(LOG_TAG, "Adding a RDO to upload: " + rdo.toString());
		
		if (rdoMap.containsKey(rdo)){	//Checking if the RDO is already uploading,
			result = false;
			Log.e(LOG_TAG, "Recording data object is already in the uploading map");
		} else {
			Log.i(LOG_TAG, "New recording to upload.");
			//Making new PostDataObject and setting fields, files and URL.
			PostDataObject pdo = new PostDataObject();

			pdo.addPostField(new PostField("JSON_OBJECT", rdo.asJSONObject().toString()));
			pdo.addPostFile(new PostFile(rdo.getRecordingFile()));
			pdo.setURL(Settings.getServerUrl()+Settings.getUploadParam());

			//Starting http post.
			Post post = new Post();
			post.execute(pdo);

			rdoMap.put(rdo, post);

			//Updating RecordingArray.
			RecordingArray.removeRecordingToUpload(rdo);
			RecordingArray.addUploadingRecording(rdo);

			result = true;
		}
		return result;
	}

	/**
	 * Updates the RecordingUploadManager. Checks recording that have finished and replaces with new recordings to upload.
	 */
	public static void update() {
		//TODO at the moment this is only called when the MainActivity.onResume() is called, thsi shoudl probably be changed to something more reliable

		List<RecordingDataObject> rdoSuccess = new ArrayList<>();
		List<RecordingDataObject> rdoFailed = new ArrayList<>();

		//Getting responses
		Map<RecordingDataObject, PostResponse> rdoResponseMap = getResponseMap();

		for (RecordingDataObject rdo : rdoResponseMap.keySet()){
			PostResponse response = rdoResponseMap.get(rdo);
			if (response != null){
				switch (response.getResponse()){
				case SUCCESS:
					Log.i(LOG_TAG, rdo.toString() + " uploaded!");
					rdoSuccess.add(rdo);
					break;
				default:
					//TODO implement other post responses, not just SUCCESS.
					Log.i(LOG_TAG, "Response of rdo " + rdo.toString() + " was " + response.getResponse());
					rdoFailed.add(rdo);
					break;
				}
			} else {
				//TODO figure what should be done when the post response is null
			}
		}

		//For each upload that was successful: Remove from RDO Map, update RecordingArray, update RDO.
		for (RecordingDataObject rdo : rdoSuccess){
			rdoMap.remove(rdo);
			RecordingArray.addUploadedRecording(rdo);
			RecordingArray.removeUploadingRecording(rdo);
			Log.i(LOG_TAG, "Recording ("+rdo.toString()+") was uploaded");	//TODO make a shorter toString option for the RDO.
			rdo.setUploaded(true);
			Recording.updateFileLocation(rdo);
		}

		//For each upload that wasn't successful: Remove from RDO Map, update RecordingArray.
		for (RecordingDataObject rdo : rdoFailed){
			rdoMap.remove(rdo);
			RecordingArray.addToUpload(rdo);
			RecordingArray.removeUploadingRecording(rdo);
		}

		//Add recordings until at max simultaneous uploads or no more recordings to upload
		while (rdoMap.size() < MAX_SIM_UPLOADS){
			RecordingDataObject rdo = RecordingArray.getRandomToUpload(); 
			if (rdo != null)
				addRDO(rdo);
			else 
				break;
		}
		
	}

	/**
	 * Goes through the recordings that have set to upload and puts data into map.
	 * @return Map of data: HashMap<RecordingDataObject, PostResponse>
	 */
	private static Map<RecordingDataObject, PostResponse> getResponseMap(){
		Map<RecordingDataObject, PostResponse> rdoResponseMap = new HashMap<>();
		
		for (RecordingDataObject rdo : rdoMap.keySet()){
			PostResponse response;
			Post post = rdoMap.get(rdo);
			switch(post.getStatus()){
			case FINISHED:
				try {
					response = post.get(100, TimeUnit.MILLISECONDS);
					rdoResponseMap.put(rdo, response);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					Log.e(LOG_TAG, e.toString());
					e.printStackTrace();
				}
				break;
			case RUNNING:
				Log.d(LOG_TAG, rdo.getRuleName()+" is still uploading.");
				rdoResponseMap.put(rdo, null);
				break;
			default:
				Log.e(LOG_TAG, "Post status is " + post.getStatus());
				rdoResponseMap.put(rdo, null);
				break;
			}
		}
		
		return rdoResponseMap;
	}
}
