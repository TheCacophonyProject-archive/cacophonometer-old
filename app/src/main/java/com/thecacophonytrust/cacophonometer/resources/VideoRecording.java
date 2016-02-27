package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VideoRecording {
    private static final String LOG_TAG = "VideoRecording.java";

    static final String folderName = "VideoRecording";

    private static Map<Integer, JSONObject> videoRecordingMap = new HashMap<>();
    private static Map<Integer, JSONObject> uploadingMap = new HashMap<>();

    public static final String API_URL = "/api/v1/videoRecordings";

    public static JSONObject getFromKey(int key) {
        if (videoRecordingMap.containsKey(key)) {
            return videoRecordingMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No videoRecording with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject videoRecording) {
        Logger.i(LOG_TAG, "Adding new videoRecording.");
        if (ResourcesUtil.findEquivalentFromMap(videoRecording, videoRecordingMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent videoRecording is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(videoRecording, videoRecordingMap);
    }

    public static int add(JSONObject videoRecording, int key) {
        Logger.i(LOG_TAG, "Adding new videoRecording.");
        if (ResourcesUtil.findEquivalentFromMap(videoRecording, videoRecordingMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent videoRecording is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(videoRecording, videoRecordingMap, key);
    }

    public static int addAndSave(JSONObject videoRecording) {
        int key = add(videoRecording);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), videoRecording);
        } else {
            Logger.d(LOG_TAG, "Not saving VideoRecording as it was already in the Map.");
        }
        return key;
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> videoFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : videoFiles.keySet()) {
            if (!videoRecordingMap.containsKey(key)) {
                count++;
                add(videoFiles.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded video file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " VideoFile objects were loaded.");
    }

    public static void finishedUpload(int key, String response) {
        //TODO parse response
        if (uploadingMap.containsKey(key)) {
            uploadingMap.remove(key);
            videoRecordingMap.remove(key);
        } else {
            Logger.e(LOG_TAG, "A VideoRecording was said to be finished uploading when it wasn't in the uploading Map");
        }
    }

    public static void errorWithUpload(int key, String message) {
        //TODO parse response
        if (uploadingMap.containsKey(key)) {
            uploadingMap.remove(key);
        } else {
            Logger.e(LOG_TAG, "A VideoRecording was said to be have an error uploading when it wasn't in the uploading Map");
        }
    }

    public static int getVideoRecordingToUpload() {
        for (int key : videoRecordingMap.keySet()) {
            if (!uploadingMap.containsKey(key)) {
                uploadingMap.put(key, videoRecordingMap.get(key));
                return key;
            }
        }
        return 0;
    }

    public static JSONObject convertForUpload(int key) {
        JSONObject apiJson = new JSONObject();
        JSONObject videoRecording = getFromKey(key);
        try {
            assert videoRecording != null;
            JSONObject videoFile = VideoFile.convertForUpload(videoRecording.getInt("videoFileKey"));
            assert videoFile != null;
            JSONObject location = Location.convertForUpload(videoRecording.getInt("locationKey"));
            assert location != null;
            JSONObject software = Software.convertForUpload(videoRecording.getInt("softwareKey"));
            assert software != null;
            JSONObject hardware = Hardware.convertForUpload(videoRecording.getInt("hardwareKey"));
            assert hardware != null;

            apiJson.put("videoFile", videoFile);
            apiJson.put("location", location);
            apiJson.put("hardware", hardware);
            apiJson.put("software", software);
            apiJson.put("batteryPercentage", videoRecording.getDouble("batteryPercentage"));

        } catch (Exception e) {
            Logger.e(LOG_TAG, e.toString());
        }
        return apiJson;
    }
}
