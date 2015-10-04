package com.thecacophonytrust.cacophonometer.util;

import android.os.Build;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class JSONMetadata {
    private static final String LOG_TAG = "JSONMetadata.java";

    private static final String HARDWARE_FOLDER_NAME = "hardware";
    private static final String SOFTWARE_FOLDER_NAME = "software";
    private static final String LOCATION_FOLDER_NAME = "location";
    private static final String RECORDING_JSON_FOLDER_NAME = "recordingJson";

    private static Random random = new Random();

    //These Maps have the JSONMetadata objects of the hardware, software and location.
    //The Key of these maps are the local id for the JSONMetadata object and not the id that is used by the database/uploadServer.
    //When a upload of a recording is done the response from the server might give a new id. This is the id to be saved in
    //the JSONMetadata object part of the map and is one used by the database.
    private static Map<Integer, JSONObject> hardwareJsonMap = new HashMap<>();
    private static Map<Integer, JSONObject> softwareJsonMap = new HashMap<>();
    private static Map<Integer, JSONObject> locationJsonMap = new HashMap<>();

    private static Map<Integer, JSONObject> recordingJSONMap = new HashMap<>();

    public static int getARecordingKey(){
        if (recordingJSONMap.isEmpty())
            return 0;
        else {
            return (int) recordingJSONMap.keySet().toArray()[0];
        }
    }

    public static JSONObject getRecording(int recordingKey){
        if (recordingJSONMap.containsKey(recordingKey))
            return recordingJSONMap.get(recordingKey);
        else {
            Log.e(LOG_TAG, "A recording was requested that is not in the recordings JSON map.");
            return null;
        }
    }

    public static int addRecording(JSONObject recordingJSON){
        int key = putNewJsonInMap(recordingJSON, recordingJSONMap);
        saveJsonObject(new File(Settings.getHomeFile(), RECORDING_JSON_FOLDER_NAME + key), recordingJSON);
        return key;
    }

    /**
     * Used to give a hardware json object a id. This is the id returned by the server.
     * @param hardwareKey key of the hardware json object to be given a new id.
     * @param newId the id to be given to the hardware json object.
     */
    public static void updateHardwareId(int hardwareKey, int newId){
        try {
            hardwareJsonMap.get(hardwareKey).put("id", newId);
        } catch(JSONException e){
            Log.e(LOG_TAG, "Error when inserting id into a hardware");
        }
    }

    /**
     * Used to give a software json object a id. This is the id returned by the server.
     * @param softwareKey key of the software json object to be given a new id.
     * @param newId the id to be given to the software json object.
     */
    public static void updateSoftwareId(int softwareKey, int newId){
        try {
            hardwareJsonMap.get(softwareKey).put("id", newId);
        } catch(JSONException e){
            Log.e(LOG_TAG, "Error when inserting id into a hardware");
        }
    }

    /**
     * Used to give a location json object a id. This is the id returned by the server.
     * @param locationKey key of the location json object to be given a new id.
     * @param newId the id to be given to the location json object.
     */
    public static void updateLocationId(int locationKey, int newId){
        try {
            hardwareJsonMap.get(locationKey).put("id", newId);
        } catch(JSONException e){
            Log.e(LOG_TAG, "Error when inserting id into a hardware");
        }
    }


    /**
     * Goes through the hardware software and location folders and loads the json objects into the jsonMaps variables.
     */
    public static void loadJsonFiles(){
        File folder = new File(Settings.getHomeFile(), HARDWARE_FOLDER_NAME);
        loadJsonIntoMapFromFolder(folder, hardwareJsonMap);
        folder = new File(Settings.getHomeFile(), SOFTWARE_FOLDER_NAME);
        loadJsonIntoMapFromFolder(folder, locationJsonMap);
        folder = new File(Settings.getHomeFile(), LOCATION_FOLDER_NAME);
        loadJsonIntoMapFromFolder(folder, locationJsonMap);
    }

    /**
     * Loads all json objects in a folder into the given map. The name of the file (minus the .json) will be used as the key in the map.
     * @param folder where the json files are in.
     * @param map to load json objects into.
     */
    private static void loadJsonIntoMapFromFolder(File folder, Map<Integer, JSONObject> map){
        JSONObject json;
        int key;
        if (!folder.exists()){
            if (!folder.mkdirs()){
                Log.e(LOG_TAG, "Error with making folders: " + folder.getAbsolutePath());
            }
            Log.i(LOG_TAG, "No hardware JSONMetadata objects were loaded.");
        } else if (!folder.isDirectory()) {
            Log.e(LOG_TAG, "Error: folder location for the hardware JSON objects was not a directory.");
            //TODO deal with this error
        } else {
            File jsonFiles[] = folder.listFiles();
            for (File f : jsonFiles){
                json = loadJsonFromFile(f);
                key = Integer.valueOf(f.getName().split("\\.")[0]);
                Log.d(LOG_TAG, "Key value of: " + key);
                map.put(key, json);
            }
        }
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
                    Log.e(LOG_TAG, "Error when seeing if two JSONMetadata objects are equal.");
                }
            }
        }
        return true;
    }

    /**
     * Returns the hardware json object with that key value
     * @param hardwareKey map key value
     * @return hardware json object
     */
    public static JSONObject getHardware(int hardwareKey){
        return hardwareJsonMap.get(hardwareKey);
    }

    /**
     * Returns the software json object with that key value
     * @param softwareKey map key value
     * @return software json object
     */
    public static JSONObject getSoftware(int softwareKey){
        return softwareJsonMap.get(softwareKey);
    }

    /**
     * Returns the location json object with that key value
     * @param locationKey map key value
     * @return location json object
     */
    public static JSONObject getLocation(int locationKey){
        if (locationKey == 0) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("longitude", 123);
                jo.put("latitude", 123);
                jo.put("utc", 123);
                jo.put("altitude", 123);
                jo.put("accuracy", 123);
                jo.put("userLocationInput", "Up a Tree");
                return jo;
            } catch (JSONException e) {
                //TODO use actual location,
                Log.i(LOG_TAG, "Using test location");
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                Log.e(LOG_TAG, sw.toString());
            }
        }
        return locationJsonMap.get(locationKey);
    }

    /**
     * Saves a json object to a .json file.
     * @param file file to save as.
     * @param jo json object to save.
     */
    public static void saveJsonObject(File file, JSONObject jo){
        File parentDir = new File(file.getParent());
        FileWriter fw;
        try {
            if (parentDir.isDirectory() || parentDir.mkdirs()){
                fw = new FileWriter(file, false);
                fw.write(jo.toString());
                fw.close();
            } else {
                Log.e(LOG_TAG, "Failed to make directory at: " + parentDir.toString());
                //TODO deal with this error properly, try to save in different directory maybe
            }
        } catch (Exception e){
            //TODO deal with exceptions
            Log.e(LOG_TAG, "Error whe saving json object to file");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
    }

    /**
     * Saves a json object to a .json file.
     * @param fileString file to save as, shown as the absolute path in string format.
     * @param jo json object to save.
     */
    public static void saveJsonObject(String fileString, JSONObject jo){
        File file = new File(fileString);
        saveJsonObject(file, jo);
    }

    /**
     * Loads a json object from a .json file.
     * @param jsonFile file of the  json object
     * @return json object
     */
    public static JSONObject loadJsonFromFile(File jsonFile){
        JSONObject jo = null;
        try {
            InputStream is = new FileInputStream(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            jo = new JSONObject(jsonString);
        } catch (Exception e){
            Log.e(LOG_TAG, "Error with loading json from file.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        return jo;
    }

    /**
     * Loads a json object from a .json file.
     * @param jsonFileLocation file of the  json object in string format.
     * @return json object
     */
    public static JSONObject loadJsonFromFile(String jsonFileLocation){
        File jsonFile = new File(jsonFileLocation);
        return loadJsonFromFile(jsonFile);
    }

    /**
     * Loads the current hardware data from the device then compares to the hardware json objects in the hardwareJsonMap.
     * If no current hardware json objects in the map match, a new one is made and the new key for the hardware is returned.
     * If the current hardware state matched another one saved then that key for that hardware is returned.
     * @return key of hardware json object
     */
    public static int getCurrentHardwareKey(){
        JSONObject currentHardware = new JSONObject();
        try{
            currentHardware.put("model", Build.MODEL);
            currentHardware.put("manufacturer", Build.MANUFACTURER);
            currentHardware.put("brand", Build.BRAND);
            currentHardware.put("microphoneId", Settings.getMicrophoneId());

        } catch (JSONException e){
            Log.e(LOG_TAG, "Error with getting phone hardware data");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        //Comparing against previous software states
        int key = findEquivalentFromMap(currentHardware, hardwareJsonMap);  //Returnes 0 if no json objects in the Map match the inputted one
        if (key == 0){
            key = putNewJsonInMap(currentHardware, hardwareJsonMap);
            //Saving to file
            File file = new File(Settings.getHomeFile(), HARDWARE_FOLDER_NAME+"/"+key);
            saveJsonObject(file, currentHardware);
        }
        return key;
    }

    /**
     * Loads the current hardware data from the device then compares to the hardware json objects in the hardwareJsonMap.
     * If no current hardware json objects in the map match, a new one is made and the new key for the hardware is returned.
     * If the current hardware state matched another one saved then that key for that hardware is returned.
     * @return key of hardware json object
     */
    public static int getCurrentSoftwareKey(){
        JSONObject currentSoftware = new JSONObject();
        try{
            currentSoftware.put("osCodename", Build.VERSION.CODENAME);
            currentSoftware.put("osIncremental", Build.VERSION.INCREMENTAL);
            currentSoftware.put("sdkInt", Build.VERSION.SDK_INT);
            currentSoftware.put("osRelease", Build.VERSION.RELEASE);
            currentSoftware.put("appVersion", Settings.getAppVersion());
        } catch (JSONException e){
            Log.e(LOG_TAG, "Error with getting phone Software data.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        //Comparing against previous software states
        int key = findEquivalentFromMap(currentSoftware, softwareJsonMap);  //Returnes 0 if no json objects in the Map match the inputted one
        if (key == 0){
            key = putNewJsonInMap(currentSoftware, softwareJsonMap);
            //Saving to file
            File file = new File(Settings.getHomeFile(), SOFTWARE_FOLDER_NAME+"/"+key);
            saveJsonObject(file, currentSoftware);
        }
        return key;
    }

    /**
     * Puts the json objects in the map finding the lowest key value to use. Returns the key of the json object.
     * @param jo json object to be inserted into map
     * @param jsonMap map to hold the json object
     * @return key for the json object that was inserted.
     */
    private static int putNewJsonInMap(JSONObject jo, Map<Integer, JSONObject> jsonMap){
        int key = 1;
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
    private static int findEquivalentFromMap(JSONObject current, Map<Integer, JSONObject> jsonMap){
        for (Integer key : jsonMap.keySet()){
            if (equalJsonObjects(current, jsonMap.get(key))){
                return key;
            }
        }
        return 0;
    }
}
