package com.thecacophonytrust.cacophonometer.resources;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VideoRules {
    private static final String LOG_TAG = "VideoRules.java";

    static final String folderName = "VideoRules";

    private static Map<Integer, JSONObject> ruleMap = new HashMap<>();
    private static Map<Integer, DataObject> ruleDOMap = new HashMap<>();   //This holds the rule as a data object. This makes it easier to use in functions and calculations.

    public static JSONObject getFromKey(int key) {
        if (ruleMap.containsKey(key)) {
            return ruleMap.get(key);
        } else {
            Logger.w(LOG_TAG, "No rule with key value " + String.valueOf(key));
            return null;
        }
    }

    public static boolean delete(int key) {
        File folder = new File(Settings.getHomeFile(), folderName);
        File f = new File(folder, key+".json");
        ruleDOMap.remove(key);
        ruleMap.remove(key);
        return f.delete();
    }

    public static int add(JSONObject rule) {
        Logger.i(LOG_TAG, "Adding new rule.");
        if (ResourcesUtil.findEquivalentFromMap(rule, ruleMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent rule is already saved.");
            return 0;
        }
        int key = ResourcesUtil.putNewJsonInMap(rule, ruleMap);
        DataObject ruleDO = new DataObject(rule);
        ruleDOMap.put(key, ruleDO);
        return key;
    }

    public static int add(JSONObject rule, int key) {
        Logger.i(LOG_TAG, "Adding new rule.");
        if (ResourcesUtil.findEquivalentFromMap(rule, ruleMap) != 0) {
            Logger.d(LOG_TAG, "Equivalent rule is already saved.");
            return 0;
        }
        DataObject ruleDO = new DataObject(rule);
        ruleDOMap.put(key, ruleDO);
        return ResourcesUtil.putNewJsonInMap(rule, ruleMap, key);
    }

    public static int addAndSave(JSONObject rule) {
        int key = add(rule);
        if (key != 0) {
            File folder = new File(Settings.getHomeFile(), folderName);
            File file = new File(folder, key + ".json");
            JsonFile.saveJSONObject(file.getAbsolutePath(), rule);
        } else {
            Logger.d(LOG_TAG, "Not saving Hardware as it was already in the Map.");
        }
        return key;
    }

    public static void loadFromFile() {
        Map<Integer, JSONObject> ruleFiles = LoadData.getResources(folderName);
        int count = 0;
        for (int key : ruleFiles.keySet()) {
            if (ruleMap.containsKey(key)) {
                Logger.d(LOG_TAG, "Loaded rule file that its key '"+key+"' is already used in the Map");
            } else {
                count++;
                add(ruleFiles.get(key), key);
            }
        }
        Logger.d(LOG_TAG, count + " VideoRules objects were loaded.");
    }

    public static JSONObject convertForUpload(int key) {
        return getFromKey(key);
    }

    static public int getNextRuleKey(){
        Logger.d(LOG_TAG, "Finding next VideoRule");
        if (ruleDOMap.size() == 0) return 0;
        int nextRuleKey = 0;
        for (int key : ruleDOMap.keySet()){
            if (nextRuleKey == 0){
                nextRuleKey = key;
            } else if (ruleDOMap.get(key).nextVideoCaptureTime().before(ruleDOMap.get(nextRuleKey).nextVideoCaptureTime()))
                nextRuleKey = key;
        }
        return nextRuleKey;
    }

    public static Map<Integer, DataObject> getRuleDOMap(){
        return ruleDOMap;
    }

    public static DataObject getRuleDO(int key){
        return ruleDOMap.get(key);
    }

    public static class DataObject {
        private static final String LOG_TAG = "VideoCaptureRuleDO.java";
        public String name = null;
        public int duration = -1;
        private Calendar startTime = null;
        public int hour = -1;
        public int minute = -1;

        public DataObject(JSONObject ruleJO) {
            try{
                name = ((String) ruleJO.get("name"));
                duration = ((Integer) ruleJO.get("duration"));
                startTime = Calendar.getInstance();
                String[] timeArgs = ruleJO.getString("startTimestamp").split(":");
                hour = Integer.valueOf(timeArgs[0]);
                minute = Integer.valueOf(timeArgs[1]);
                assert (0 <= hour && hour <= 23);
                assert (0 <= minute && minute <= 59);
                Logger.d(LOG_TAG, "Time args: " + timeArgs[0] + ":" + timeArgs[1]);
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
            } catch (Exception e){
                Logger.e(LOG_TAG, "Error when making new rule from JSON file.");
                Logger.exception(LOG_TAG, e);
            }
        }

        public Calendar nextVideoCaptureTime() {
            Calendar now = Calendar.getInstance();
            while (startTime.before(now)) {
                startTime.add(Calendar.DATE, 1);
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
            }
            return startTime;
        }

        public String getName(){
            return name;
        }

        public int getDuration(){
            return duration;
        }
    }
}
