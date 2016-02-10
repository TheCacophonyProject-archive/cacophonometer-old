package com.thecacophonytrust.cacophonometer.resources;

import android.os.Build;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Software {
    private static final String LOG_TAG = "Software.java";

    static final String folderName = "Software";

    private static Map<Integer, JSONObject> softwareMap = new HashMap<>();

    public static JSONObject getFromKey(int key) {
        if (softwareMap.containsKey(key)) {
            return softwareMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No software with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject software) {
        Logger.i(LOG_TAG, "Adding new software.");
        if (ResourcesUtil.findEquivalentFromMap(software, softwareMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent software is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(software, softwareMap);
    }

    public static int add(JSONObject software, int key) {
        Logger.i(LOG_TAG, "Adding new software.");
        if (ResourcesUtil.findEquivalentFromMap(software, softwareMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent software is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(software, softwareMap, key);
    }

    public static int addAndSave(JSONObject software) {
        int key = add(software);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), software);
        } else {
            Logger.d(LOG_TAG, "Not saving Software as it was already in the Map.");
        }
        return key;
    }

    public static int getCurrentKey(){
        JSONObject currentSoftware = new JSONObject();
        try {
            currentSoftware.put("osCodename", Build.VERSION.CODENAME);
            currentSoftware.put("osIncremental", Build.VERSION.INCREMENTAL);
            currentSoftware.put("sdkInt", Build.VERSION.SDK_INT);
            currentSoftware.put("osRelease", Build.VERSION.RELEASE);
            currentSoftware.put("version", Settings.getAppVersion());
        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error with getting phone software data");
            Logger.exception(LOG_TAG, e);
        }
        int equiv = ResourcesUtil.findEquivalentFromMap(currentSoftware, softwareMap);
        if (equiv != 0) {
            return equiv;
        } else {
            return addAndSave(currentSoftware);
        }
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> audioFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : audioFiles.keySet()) {
            if (!softwareMap.containsKey(key)) {
                count++;
                add(audioFiles.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded audio file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " AudioFile objects were loaded.");
    }

    public static JSONObject convertForUpload(int key) {
        return getFromKey(key);
    }
}
