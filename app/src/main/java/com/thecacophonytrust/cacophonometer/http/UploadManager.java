package com.thecacophonytrust.cacophonometer.http;


import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JSONMetadata;
import com.thecacophonytrust.cacophonometer.util.ThreadExecutor;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UploadManager {
    private static final String LOG_TAG = "UploadManager.java";

    private static final int MAX_SIMUL_UPLOAD = 1;

    private static ThreadExecutor threadExecutor = new ThreadExecutor();

    public static void update(){
        Log.i(LOG_TAG, "Updating upload manager.");
        if (threadExecutor.activeThreads() >= MAX_SIMUL_UPLOAD){
            Log.d(LOG_TAG, "Device is already uploading");
        } else {
            startNewUpload();
        }
    }

    public static void finishedUpload(int responseCode, String response, int recordingKey){
        if (responseCode == -1){
            Log.e(LOG_TAG, "Invalid response code.");
        } else if (responseCode != HttpsURLConnection.HTTP_OK){
            Log.e(LOG_TAG, "Error with upload upload.");
        } else {
            Log.i(LOG_TAG, "Successful upload.");
            Log.d(LOG_TAG, response);
        }
    }

    public static void errorWithUpload(int recordingKey, String message){
        Log.e(LOG_TAG, "Error with upload. RecordingKey: " + recordingKey +", Message: "+ message);
    }

    private static void startNewUpload(){
        int recordingKey = JSONMetadata.getARecordingKey();
        if (recordingKey == 0){
            Log.d(LOG_TAG, "No recording found to upload");
            return;
        }
        UploadRunnable uploadRunnable = new UploadRunnable();
        String urlString = null;
        try {
            urlString = Settings.getServerUrl() + Settings.getUploadParam();
            URL url = new URL(urlString);
            uploadRunnable.setUrl(url);
            uploadRunnable.setRecordingKey(recordingKey);
            threadExecutor.execute(uploadRunnable);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with generating upload URL: " + urlString);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
    }
}
