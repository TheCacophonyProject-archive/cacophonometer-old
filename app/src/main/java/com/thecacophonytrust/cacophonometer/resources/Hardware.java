package com.thecacophonytrust.cacophonometer.resources;

import android.os.Build;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hardware {
    private static final String LOG_TAG = "Hardware.java";

    static final String folderName = "Hardware";

    private static Map<Integer, JSONObject> hardwareMap = new HashMap<>();

    public static JSONObject getFromKey(int key) {
        if (hardwareMap.containsKey(key)) {
            return hardwareMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No hardware with key value " + String.valueOf(key));
            return null;
        }
    }

    public static int add(JSONObject hardware) {
        Logger.i(LOG_TAG, "Adding new hardware.");
        if (ResourcesUtil.findEquivalentFromMap(hardware, hardwareMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent hardware is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(hardware, hardwareMap);
    }

    public static int add(JSONObject hardware, int key) {
        Logger.i(LOG_TAG, "Adding new hardware.");
        if (ResourcesUtil.findEquivalentFromMap(hardware, hardwareMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent hardware is already saved.");
            return 0;
        }
        return ResourcesUtil.putNewJsonInMap(hardware, hardwareMap, key);
    }

    public static int addAndSave(JSONObject hardware) {
        int key = add(hardware);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), hardware);
        } else {
            Logger.d(LOG_TAG, "Not saving Hardware as it was already in the Map.");
        }
        return key;
    }

    public static int getCurrentKey(){
        JSONObject currentHardware = new JSONObject();
        try {
            currentHardware.put("model", Build.MODEL);
            currentHardware.put("manufacturer", Build.MANUFACTURER);
            currentHardware.put("brand", Build.BRAND);
        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error with getting phone hardware data");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.e(LOG_TAG, sw.toString());
        }
        int equiv = ResourcesUtil.findEquivalentFromMap(currentHardware, hardwareMap);
        if (equiv != 0) {
            return equiv;
        } else {
            return addAndSave(currentHardware);
        }
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> audioFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : audioFiles.keySet()) {
            if (!hardwareMap.containsKey(key)) {
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
