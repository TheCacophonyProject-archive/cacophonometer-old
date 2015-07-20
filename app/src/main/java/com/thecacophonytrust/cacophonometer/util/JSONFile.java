package com.thecacophonytrust.cacophonometer.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

public class JSONFile {

    private static final String LOG_TAG = "JSONFile.java";

    public static JSONObject getJSON(String jsonFile){
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
            //TODO deal with exceptions
            Log.e(LOG_TAG, e.toString());
        }
        return jo;
    }

    public static void saveJSONObject(String file, JSONObject jsonObject){
        Log.d(LOG_TAG, "Saving JSON file at '"+file+"'");
        File f = new File(file);
        File parent = new File(f.getParent());
        FileWriter fw;
        try {
            if (!parent.isDirectory())
                parent.mkdirs();
            fw = new FileWriter(f, false);
            fw.write(jsonObject.toString());
            fw.close();
        } catch (Exception e){
            //TODO deal with exceptions
            Log.e(LOG_TAG, e.toString());
        }
    }

}
