package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioRecording {
    private static final String LOG_TAG = "AudioRecording.java";

    static final String folderName = "AudioRecording";

    private static Map<Integer, JSONObject> audioRecordingMap = new HashMap<>();
    private static Map<Integer, JSONObject> uploadingMap = new HashMap<>();

    public static final String API_URL = "/api/v1/audiorecording";

    public static JSONObject getFromKey(int key) {
        if (audioRecordingMap.containsKey(key)) {
            return audioRecordingMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No audioRecording with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject audioRecording) {
        Logger.i(LOG_TAG, "Adding new audioRecording.");
        if (ResourcesUtil.findEquivalentFromMap(audioRecording, audioRecordingMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent audioRecording is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(audioRecording, audioRecordingMap);
    }

    public static int add(JSONObject audioRecording, int key) {
        Logger.i(LOG_TAG, "Adding new audioRecording.");
        if (ResourcesUtil.findEquivalentFromMap(audioRecording, audioRecordingMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent audioRecording is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(audioRecording, audioRecordingMap, key);
    }

    public static int addAndSave(JSONObject audioRecording) {
        int key = add(audioRecording);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), audioRecording);
        } else {
            Logger.d(LOG_TAG, "Not saving AudioRecording as it was already in the Map.");
        }
        return key;
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> audioFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : audioFiles.keySet()) {
            if (!audioRecordingMap.containsKey(key)) {
                count++;
                add(audioFiles.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded audio file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " AudioFile objects were loaded.");
    }

    public static void finishedUpload(int key, String response) {
        //TODO parse response
        if (uploadingMap.containsKey(key)) {
            uploadingMap.remove(key);
            delete(key);
        } else {
            Logger.e(LOG_TAG, "A AudioRecording was said to be finished uploading when it wasn't in the uploading Map");
        }
    }

    public static void errorWithUpload(int key, String message) {
        //TODO parse response
        if (uploadingMap.containsKey(key)) {
            uploadingMap.remove(key);
        } else {
            Logger.e(LOG_TAG, "A AudioRecording was said to be have an error uploading when it wasn't in the uploading Map");
        }
    }

    public static int getAudioRecordingToUpload() {
        for (int key : audioRecordingMap.keySet()) {
            if (!uploadingMap.containsKey(key)) {
                uploadingMap.put(key, audioRecordingMap.get(key));
                return key;
            }
        }
        return 0;
    }

    public static JSONObject convertForUpload(int key) {
        return getFromKey(key);
    }

    /**
     * Will delete Audio recording, json and file, from the device.
     * @param key
     */
    public static void delete(int key) {
        try {
            String filePath = audioRecordingMap.get(key).getString("localFilePath");
            File f = new File(filePath);
            f.delete();
        } catch (JSONException e) {
            Logger.exception(LOG_TAG, e);
        }
        File jsonFolder = new File(Settings.getHomeFile(), folderName);
        File jsonFile = new File(jsonFolder, key + ".json");
        jsonFile.delete();
        audioRecordingMap.remove(key);

    }
}
