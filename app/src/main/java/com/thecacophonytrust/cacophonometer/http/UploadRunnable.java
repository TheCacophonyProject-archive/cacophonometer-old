package com.thecacophonytrust.cacophonometer.http;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.enums.PostErrorType;
import com.thecacophonytrust.cacophonometer.util.JSONMetadata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UploadRunnable implements Runnable{
    private static final String LOG_TAG = "UploadRunnable.java";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";

    private URL url = null;
    private int recordingKey = 0;
    private boolean finished = false;


    @Override
    public void run(){
        JSONObject json = new JSONObject();

        JSONObject mainData = JSONMetadata.getRecording(recordingKey);
        JSONObject hardware;
        JSONObject software;
        JSONObject location;

        if (mainData == null){
            finished = true;
            UploadManager.errorWithUpload(recordingKey, "JSON for given key was not found in JSONMetadata");
            return;
        }

        File file;
        String fileName = null;
        try {
            json.put("mainData", mainData);
            json.put("hardware", JSONMetadata.getHardware((int) mainData.get("hardwareKey")));
            json.put("software", JSONMetadata.getSoftware((int) mainData.get("softwareKey")));
            json.put("location", JSONMetadata.getLocation((int) mainData.get("locationKey")));
            json.put("deviceId", Settings.getDeviceId());
            fileName = (String) mainData.get("fileName");
            file = new File(Settings.getRecordingsFolder(), fileName);
            if (!file.exists()){
                Log.e(LOG_TAG, "File is not found.");
                finished = true;
                UploadManager.errorWithUpload(recordingKey, "Recording file not found.");
                return;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing json");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
            finished = true;
            UploadManager.errorWithUpload(recordingKey, "parsing JSON error.");
            return;
        }

        String response = "";
        int responseCode = -1;
        //private boolean startPost(List<PostField> postFields, List<PostFile> postFiles, URL url){
        Log.d(LOG_TAG, "Starting post");
        Log.d(LOG_TAG, "URL: " + url);
        String boundary = Long.toHexString(System.currentTimeMillis());
        try {
            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);

            urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //urlConn.getOutputStream();

            DataOutputStream request = new DataOutputStream (urlConn.getOutputStream ());
            String key = "JSON";
            String value = json.toString();
            request.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            request.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_END);
            request.writeBytes(LINE_END);
            request.writeBytes(value + LINE_END);

            request.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            request.writeBytes("Content-Disposition: form-data; name=\"recording\";filename=\""+fileName+"\"" + LINE_END); //TODO change file name etc...
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
            Log.d(LOG_TAG, "Response code: " + responseCode);
            response = "";
            String line;
            try {
                while ((line = inputStream.readLine()) != null){
                    response += line;
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, e.toString());
            }


            /*
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            //urlConn.setRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //urlConn.setRequestMethod("POST");

            request = new DataOutputStream(urlConn.getOutputStream());

            //Write field data
            String key = "JSON";
            String value = json.toString();
            Log.v(LOG_TAG, "json:" + value);
            request.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_END);
            request.writeBytes(LINE_END);
            request.writeBytes("aValue" + LINE_END);

            request.writeBytes(TWO_HYPHENS + boundary + LINE_END);

            //Write file data
            request.writeBytes("Content-Disposition: form-data; name=\"recording\";filename=\"" + fileName + "\"" + LINE_END);
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


            responseCode = urlConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader (urlConn.getInputStream()));
                String line;
                while ((line=inputStream.readLine()) != null){
                    response+=line;
                }

            }
            */
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with uploading data.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        finished = true;
        UploadManager.finishedUpload(responseCode, response, recordingKey);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setRecordingKey(int recordingKey) {
        this.recordingKey = recordingKey;
    }

    public boolean isFinished(){
        return finished;
    }
}
