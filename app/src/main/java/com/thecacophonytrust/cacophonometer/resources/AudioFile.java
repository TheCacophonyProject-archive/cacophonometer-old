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

public class AudioFile {
    private static final String LOG_TAG = "AudioFile.java";

    static final String folderName = "AudioFile";

    private static Map<Integer, JSONObject> audioFileMap = new HashMap<>();

    public static JSONObject getFromKey(int key) {
        if (audioFileMap.containsKey(key)) {
            return audioFileMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No hardware with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject audioFile) {
        Logger.i(LOG_TAG, "Adding new audioFile.");
        if (ResourcesUtil.findEquivalentFromMap(audioFile, audioFileMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent audioFile is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(audioFile, audioFileMap);
    }

    public static int add(JSONObject audioFile, int key) {
        Logger.i(LOG_TAG, "Adding new audioFile.");
        if (ResourcesUtil.findEquivalentFromMap(audioFile, audioFileMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent audioFile is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(audioFile, audioFileMap, key);
    }



    public static int addAndSave(JSONObject audioFile) {
        int key = add(audioFile);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), audioFile);
        } else {
            Logger.d(LOG_TAG, "Not saving AudioFile as it was already in the Map.");
        }
        return key;
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> audioFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : audioFiles.keySet()) {
            if (!audioFileMap.containsKey(key)) {
                count++;
                add(audioFiles.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded audio file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " AudioFile objects were loaded.");
    }


    /**
     * This will convert the audioFile into a JSON that is ready for upload.
     * In this case the localFileName field is removed.
     * @param key of audioFile.
     * @return audioFile JSON ready for upload.
     */
    public static JSONObject convertForUpload(int key) {
        JSONObject audioFile = null;
        try {
            audioFile = new JSONObject(getFromKey(key).toString());
        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error when getting ");
        }
        if (audioFile == null) return null;
        audioFile.remove("localFilePath");
        return audioFile;
    }
}
