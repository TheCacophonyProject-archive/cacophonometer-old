package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.GPS;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {
    private static final String LOG_TAG = "Location.java";

    static final String folderName = "Location";

    //static DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.UK);

    private static Map<Integer, JSONObject> locationMap = new HashMap<>();
    private static int mostRecentLocation = 0;
    private static GPS gps = new GPS();

    /**
     * Gets the location represented by the kay value.
     * @param key of location.
     * @return a Location JSON object.
     */
    public static JSONObject getFromKey(int key) {
        if (locationMap.containsKey(key)) {
            return locationMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No location with key value " + String.valueOf(key));
            return null;
        }
    }

    /**
     * Adds the location to the Location map.
     * Will also update most recent location.
     */
    public static int add(JSONObject location) {
        Logger.i(LOG_TAG, "Adding new Location.");
        boolean newMostRecentLocation = false;

        if (ResourcesUtil.findEquivalentFromMap(location, locationMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent location is already saved.");
            return 0;
        }
        if (mostRecentLocation == 0) {
            newMostRecentLocation = true;
        } else {
            try {
                Date addedDate = ResourcesUtil.iso8601.parse((String) location.get("timestamp"));
                Date mostRecentDate = ResourcesUtil.iso8601.parse((String) getMostRecent().get("timestamp"));
                if (addedDate.after(mostRecentDate)) {
                    newMostRecentLocation = true;
                }
            } catch (Exception e) {
                Logger.e(LOG_TAG, "Error with parsing timestamp from location");
                Logger.exception(LOG_TAG, e);
            }
        }
        int key = ResourcesUtil.putNewJsonInMap(location, locationMap);
        if (newMostRecentLocation) {
            mostRecentLocation = key;
        }
        return key;
    }

    public static int add(JSONObject location, int key) {
        Logger.i(LOG_TAG, "Adding new Location.");
        boolean newMostRecentLocation = false;

        if (ResourcesUtil.findEquivalentFromMap(location, locationMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent location is already saved.");
            return 0;
        }
        if (mostRecentLocation == 0) {
            newMostRecentLocation = true;
        } else {
            try {
                Date addedDate = ResourcesUtil.iso8601.parse((String) location.get("timestamp"));
                Date mostRecentDate = ResourcesUtil.iso8601.parse((String) getMostRecent().get("timestamp"));
                if (addedDate.after(mostRecentDate)) {
                    newMostRecentLocation = true;
                }
            } catch (Exception e) {
                Logger.e(LOG_TAG, "Error with parsing timestamp from location");
                Logger.exception(LOG_TAG, e);
            }
        }
        int newkey = ResourcesUtil.putNewJsonInMap(location, locationMap, key);
        if (newMostRecentLocation) {
            mostRecentLocation = newkey;
        }
        return newkey;
    }

    public static void addAndSave(JSONObject location) {
        int key = add(location);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), location);
        } else {
            Logger.d(LOG_TAG, "Not saving location as it was already in the Map.");
        }
    }

    /**
     * Gets the key of the most recent Location.
     * @return key of location.
     */
    public static int getMostRecentKey() {
        return mostRecentLocation;
    }

    /**
     * Gets the most recent Location JSON object.
     * @return JSON of location.
     */
    public static JSONObject getMostRecent() {
        return getFromKey(mostRecentLocation);
    }

    /**
     * Loads the Location from the Location resource folder into the Location Map.
     */




    public static void loadFromFile() {
        Map<Integer, JSONObject> locations = LoadData.getResources(folderName);
        int count = 0;
        for (int key : locations.keySet()) {
            if (!locationMap.containsKey(key)) {
                try {
                    Logger.v(LOG_TAG, locations.get(key).getString("timestamp"));
                } catch (Exception e) {
                    Logger.w(LOG_TAG, "Error with getting timestamp from location.");
                }
                count++;
                add(locations.get(key), key);
            } else {
                Logger.d(LOG_TAG, "Loaded audio file that its key is already used in the Map");
            }
        }
        Logger.d(LOG_TAG, count + " AudioFile objects were loaded.");
    }

    /**
     * Requests a new Location.
     * After the nw location is made it will be saved as the most recent location.
     */
    public static void getNewLocation(){
        gps.update(null);
    }

    public static JSONObject convertForUpload(int key) {
        return getFromKey(key);
    }
}
