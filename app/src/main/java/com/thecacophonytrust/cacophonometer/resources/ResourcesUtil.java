package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class ResourcesUtil {
    private static final String LOG_TAG = "ResourcesUtil.java";

    public static DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.UK);

    /**
     * Puts the json objects in the map finding the lowest key value to use. Returns the key of the json object.
     * @param jo json object to be inserted into map
     * @param jsonMap map to hold the json object
     * @return key for the json object that was inserted.
     */
    public static int putNewJsonInMap(JSONObject jo, Map<Integer, JSONObject> jsonMap){
        int key = 1;
        while(jsonMap.keySet().contains(key)){
            key++;
        }
        jsonMap.put(key, jo);
        return key;
    }

    /**
     * Puts the json objects in the map finding the lowest key value to use. Returns the key of the json object.
     * @param jo json object to be inserted into map
     * @param jsonMap map to hold the json object
     * @return key for the json object that was inserted.
     */
    public static int putNewJsonInMap(JSONObject jo, Map<Integer, JSONObject> jsonMap, int key){
        while(jsonMap.keySet().contains(key)){
            key++;
        }
        jsonMap.put(key, jo);
        return key;
    }


    /**
     * Compares the inputted json object against the ones in the jsonMap.
     * Returns the key id of a json object that is equivalent to it.
     * If none are found to be equivalent then 0 is returned.
     * @param current json object
     * @param jsonMap current json object is compared against the json objects in this map.
     * @return 0 if no json object in the map is the same,
     *          if a json value is found to be the same the key value of the json object from the map is returned.
     */
    public static int findEquivalentFromMap(JSONObject current, Map<Integer, JSONObject> jsonMap){
        for (Integer key : jsonMap.keySet()){
            if (equalJsonObjects(current, jsonMap.get(key))){
                return key;
            }
        }
        return 0;
    }

    /**
     * Compares two json objects and returns true if they are equivalent. the id value is ignored for this.
     * @param json1 to compare with.
     * @param json2 to compare against.
     * @return true if they are equivalent (not looking at the id value)
     */
    private static boolean equalJsonObjects(JSONObject json1, JSONObject json2){
        Iterator<String> keys = json1.keys();

        while( keys.hasNext() ) {
            String key = keys.next();
            if (!key.equals("id")) {
                try {
                    if (!json2.has(key)) {
                        return false;
                    }
                    if (json1.get(key) != json2.get(key)) {
                        return false;
                    }
                } catch (JSONException e) {
                    Logger.e(LOG_TAG, "Error when seeing if two JsonMetadata objects are equal.");
                    Logger.exception(LOG_TAG, e);
                }
            }
        }
        return true;
    }


}
