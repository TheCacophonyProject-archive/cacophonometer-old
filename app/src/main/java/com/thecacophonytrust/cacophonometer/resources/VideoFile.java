package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VideoFile {
    private static final String LOG_TAG = "VideoFile.java";

    static final String folderName = "VideoFile";

    private static Map<Integer, JSONObject> videoFileMap = new HashMap<>();

    public static JSONObject getFromKey(int key) {
        if (videoFileMap.containsKey(key)) {
            return videoFileMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No hardware with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject videoFile) {
        Logger.i(LOG_TAG, "Adding new videoFile.");
        if (ResourcesUtil.findEquivalentFromMap(videoFile, videoFileMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent videoFile is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(videoFile, videoFileMap);
    }

    public static int add(JSONObject videoFile, int key) {
        Logger.i(LOG_TAG, "Adding new videoFile.");
        if (ResourcesUtil.findEquivalentFromMap(videoFile, videoFileMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent videoFile is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(videoFile, videoFileMap, key);
    }



    public static int addAndSave(JSONObject videoFile) {
        int key = add(videoFile);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), videoFile);
        } else {
            Logger.d(LOG_TAG, "Not saving Video File as it was already in the Map.");
        }
        return key;
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> videoFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : videoFiles.keySet()) {
            if (!videoFileMap.containsKey(key)) {
                count++;
                add(videoFiles.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded video file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " VideoFile objects were loaded.");
    }


    /**
     * This will convert the videoFile into a JSON that is ready for upload.
     * In this case the localFileName field is removed.
     * @param key of videoFile.
     * @return videoFile JSON ready for upload.
     */
    public static JSONObject convertForUpload(int key) {
        JSONObject videoFile = null;
        try {
            videoFile = new JSONObject(getFromKey(key).toString());
        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error when getting ");
        }
        if (videoFile == null) return null;
        videoFile.remove("localFilePath");
        return videoFile;
    }
}
