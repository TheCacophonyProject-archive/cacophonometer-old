package com.thecacophonytrust.cacophonometer.http;

import com.thecacophonytrust.cacophonometer.resources.AudioFile;
import com.thecacophonytrust.cacophonometer.resources.AudioRecording;
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

public class AudioUploadRunnable implements Runnable{
    private static final String LOG_TAG = "AudioUploadRunnable.java";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";

    private URL url = null;
    private int recordingKey = 0;

    @Override
    public void run(){

        JSONObject data = AudioRecording.convertForUpload(recordingKey);
        String filePath;
        File file;
        try {
            filePath = data.getString("localFilePath");
        } catch (Exception e) {
            Logger.exception(LOG_TAG, e);
            UploadManager.errorWithAudioUpload(recordingKey, "Error when setting variables to upload.");
            return;
        }

        file = new File(filePath);
        if (!file.exists()){
            Logger.e(LOG_TAG, "File is not found.");
            UploadManager.errorWithAudioUpload(recordingKey, "Recording file not found.");
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
            request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""+file.getName()+"\"" + LINE_END); //TODO change file name etc...
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
                UploadManager.errorWithAudioUpload(recordingKey, "Error with getting response from server");
                return;
            }
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Error with uploading data.");
            Logger.exception(LOG_TAG, e);
            UploadManager.errorWithAudioUpload(recordingKey, "Error with uploading data");
            return;
        }
        UploadManager.finishedAudioUpload(responseCode, response, recordingKey);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setAudioRecordingKey(int recordingKey) {
        this.recordingKey = recordingKey;
    }
}
