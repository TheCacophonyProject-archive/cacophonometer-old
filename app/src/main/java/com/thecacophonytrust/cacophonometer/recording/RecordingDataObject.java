package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordingDataObject {

	/**
	 * This class holds the relevant data for a recording, deviceId, UTC...
	 */

	private static final String LOG_TAG = "RecordingDataObjec.java";	//LOG_TAG for android.util.Log can only be 23 characters, so convention was not followed for this class.


    private String ruleName = "";
    private int deviceId = 0;
    private String fileExtension = "";
    private long startTimeUtc = 0;
    private long stopTimeUTC = 0;
    private int duration = 0;
    private int bitRate = 0;

    private File recordingFile = null;

	private int hardwareKey = 0;
	private int softwareKey = 0;
	private int locationKey = 0;


	/**
	 * Recording Data Object (RDO), this holds the relevant data of a recording.
	 * @param ruleName of rule that started recording.
	 */
	public RecordingDataObject(String ruleName, int deviceId, int duration, long startTimeUTC, int bitRate, String fileExtension){
		this.ruleName = ruleName;
        this.deviceId = deviceId;
        this.duration = duration;
        this.startTimeUtc = startTimeUTC;
        this.bitRate = bitRate;
        this.fileExtension = fileExtension;
		Log.d(LOG_TAG, "RecordingDataObject constructed for rule '"+ruleName+"'");
	}

    public void setHardwareKey(int hardwareKey){
        this.hardwareKey = hardwareKey;
    }

    public void setSoftwareKey(int softwareKey){
        this.softwareKey = softwareKey;
    }

    public void setLocationKey(int locationKey){
        this.locationKey = locationKey;
    }

    public void setStopTimeUTC(long stopTimeUTC){
        this.stopTimeUTC = stopTimeUTC;
    }
    public boolean isValid(){
        //TODO
        return true;
    }

    public String getRuleName(){
        return ruleName;
    }

    public boolean locationNotSet(){
        return (locationKey == 0);
    }

    public File getRecordingFile(){
        if (recordingFile == null)
            recordingFile = new File(Settings.getRecordingsFolder(), getFileName());
        return recordingFile;
    }

    public String getFileName(){
        return String.valueOf(deviceId)+"_"+String.valueOf(startTimeUtc)+"."+fileExtension;
    }

    public JSONObject exportAsJSONObject(){
        JSONObject jo = new JSONObject();
        try {
            jo.put("deviceId", deviceId);
            jo.put("fileName", getFileName());
            jo.put("fileExtension", fileExtension);
            jo.put("startTimeUtc", startTimeUtc);
            jo.put("duration", duration);
            jo.put("ruleName", ruleName);
            jo.put("bitRate", bitRate);
            jo.put("hardwareKey", hardwareKey);
            jo.put("softwareKey", softwareKey);
            jo.put("locationKey", locationKey);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error with exporting RecordingDataObject as a JSON Object.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        return jo;
    }

	@Override
	public String toString() {
        return exportAsJSONObject().toString();
	}
}