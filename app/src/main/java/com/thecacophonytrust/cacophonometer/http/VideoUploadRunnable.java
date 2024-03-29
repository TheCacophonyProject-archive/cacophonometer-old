package com.thecacophonytrust.cacophonometer.http;

import com.thecacophonytrust.cacophonometer.resources.VideoFile;
import com.thecacophonytrust.cacophonometer.resources.VideoRecording;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class was used to upload video recordings. But is no longer used/maintained.
 * We use to have an Arduino that had a PIR connected to it (motion sensor) and when the PIR detect
 * motion the Arduino would connect to the Android phone telling it to start recording a video.
 * This class is no longer used as we are using a Raspberry Pi for video recording and other
 * experimental ideas.
 */
public class VideoUploadRunnable implements Runnable {
    private static final String LOG_TAG = "VideoUploadRunnable.java";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";

    private URL url = null;
    private int recordingKey = 0;

    @Override
    public void run(){

        JSONObject data = VideoRecording.convertForUpload(recordingKey);
        String filePath;
        File file;
        try {
            JSONObject videoRecording = VideoRecording.getFromKey(recordingKey);
            assert videoRecording != null;
            JSONObject videoFile = VideoFile.getFromKey(videoRecording.getInt("videoFileKey"));
            assert videoFile != null;
            Logger.d(LOG_TAG, videoFile.toString());
            filePath = videoFile.getString("localFilePath");
        } catch (Exception e) {
            Logger.e(LOG_TAG, e.toString());
            Logger.exception(LOG_TAG, e);
            UploadManager.errorWithVideoUpload(recordingKey, "Error when setting variables to upload.");
            return;
        }

        file = new File(filePath);
        if (!file.exists()){
            Logger.e(LOG_TAG, "File is not found.");
            UploadManager.errorWithVideoUpload(recordingKey, "Recording file not found.");
            return;
        }

        String response;
        int responseCode;
        Logger.d(LOG_TAG, "Starting post");
        Logger.d(LOG_TAG, "URL: " + url);
        String boundary = Long.toHexString(System.currentTimeMillis());
        try {
            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);

            urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream (urlConn.getOutputStream ());
            String key = "data";
            String value = data.toString();
            Logger.d(LOG_TAG, value);
            request.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            request.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_END);
            request.writeBytes(LINE_END);
            request.writeBytes(value + LINE_END);

            request.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"videoFile\"" + LINE_END); //TODO change file name etc...
            request.writeBytes(LINE_END);
            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesRead, bytesAvailable, bufferSize;
            int maxBufferSize = 1024 * 1024;
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer;
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                request.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            request.writeBytes(LINE_END + TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_END);
            request.flush();
            request.close();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            responseCode = ((HttpURLConnection) urlConn).getResponseCode();
            Logger.d(LOG_TAG, "Response code: " + responseCode);
            response = "";
            String line;
            try {
                while ((line = inputStream.readLine()) != null){
                    response += line;
                }
            } catch (IOException e) {
                Logger.d(LOG_TAG, e.toString());
                Logger.exception(LOG_TAG, e);
                UploadManager.errorWithVideoUpload(recordingKey, "Error with getting response from server");
                return;
            }
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Error with uploading data.");
            Logger.exception(LOG_TAG, e);
            UploadManager.errorWithVideoUpload(recordingKey, "Error with uploading data");
            return;
        }
        UploadManager.finishedVideoUpload(responseCode, response, recordingKey);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setVideoRecordingKey(int recordingKey) {
        this.recordingKey = recordingKey;
    }
}
