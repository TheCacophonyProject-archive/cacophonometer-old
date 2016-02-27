package com.thecacophonytrust.cacophonometer.http;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.resources.AudioRecording;
import com.thecacophonytrust.cacophonometer.resources.VideoRecording;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.ThreadExecutor;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UploadManager {
    private static final String LOG_TAG = "UploadManager.java";

    private static final int MAX_SIMUL_UPLOAD = 1;

    private static ThreadExecutor threadExecutor = new ThreadExecutor();

    public static void update(){
        Logger.i(LOG_TAG, "Updating upload manager.");
        if (threadExecutor.activeThreads() >= MAX_SIMUL_UPLOAD){
            Logger.d(LOG_TAG, "Device is already uploading");
        } else {
            startNewUpload();
        }
    }

    /**
     *
     * @param responseCode http response code from server.
     * @param response String of the response from the server.
     * @param recordingKey The recordingKey of the recording that was uploaded.
     */
    public static void finishedUpload(int responseCode, String response, int recordingKey){
        if (responseCode != HttpsURLConnection.HTTP_OK){
            Logger.e(LOG_TAG, "Error with upload upload. Response code of " + Integer.toString(responseCode));
            AudioRecording.errorWithUpload(recordingKey, response);
        } else {
            Logger.i(LOG_TAG, "Successful upload.");
            Logger.d(LOG_TAG, "Response from server: " + response);
            AudioRecording.finishedUpload(recordingKey, response);
        }
    }

    public static void errorWithUpload(int recordingKey, String message){
        AudioRecording.errorWithUpload(recordingKey, message);
        Logger.e(LOG_TAG, "Error with upload. RecordingKey: " + recordingKey + ", Message: " + message);
    }

    /**
     * startNewUpload will request a recordingKey from the JsonMetadata Class to upload, a key value of 0 means nothing to upload.
     * After this a AudioUploadRunnable is created and appropriate values are set, url and recordingKey.
     * Finally the AudioUploadRunnable is executed in the Static ThreadExecutor variable in UploadManager.
     */
    private static void startNewUpload(){
        int audioRecordingKey = AudioRecording.getAudioRecordingToUpload();
        int videoRecordingKey = VideoRecording.getVideoRecordingToUpload();
        if (audioRecordingKey == 0 && videoRecordingKey == 0){
            Logger.i(LOG_TAG, "No recording found to upload");
            return;
        }
        if (audioRecordingKey != 0) {
            AudioUploadRunnable audioUploadRunnable = new AudioUploadRunnable();
            String urlString = null;
            try {
                urlString = Settings.getServerUrl() + AudioRecording.API_URL;
                URL url = new URL(urlString);
                audioUploadRunnable.setUrl(url);
                audioUploadRunnable.setAudioRecordingKey(audioRecordingKey);
                threadExecutor.execute(audioUploadRunnable);
            } catch (MalformedURLException e) {
                Logger.e(LOG_TAG, "Error with generating upload URL: " + urlString);
                Logger.exception(LOG_TAG, e);
            }
        }
        if (videoRecordingKey != 0) {
            VideoUploadRunnable videoUploadRunnable = new VideoUploadRunnable();
            String urlString = null;
            try {
                urlString = Settings.getServerUrl() + VideoRecording.API_URL;
                URL url = new URL(urlString);
                videoUploadRunnable.setUrl(url);
                videoUploadRunnable.setVideoRecordingKey(videoRecordingKey);
                threadExecutor.execute(videoUploadRunnable);
            } catch (MalformedURLException e) {
                Logger.e(LOG_TAG, "Error with generating upload URL: " + urlString);
                Logger.exception(LOG_TAG, e);
            }
        }

    }
}
