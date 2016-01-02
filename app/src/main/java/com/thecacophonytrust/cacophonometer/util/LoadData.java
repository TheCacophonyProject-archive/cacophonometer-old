package com.thecacophonytrust.cacophonometer.util;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import com.thecacophonytrust.cacophonometer.Settings;

import com.thecacophonytrust.cacophonometer.enums.FileFilterType;

import org.json.JSONObject;

public class LoadData {

	/**
	 * This class loads the data from the text files and makes rules and RecordingDataObjects from them.
	 */
	
	private static final String LOG_TAG = "LoadData.java";

	/**
	 * Loads the data from the settings JSON file.
	 */
	public static void loadSettings(){
		File settingFile = Settings.getSettingsFile();
		if (!settingFile.isFile()) {
			Logger.d(LOG_TAG, "Settings file was not found at: " + settingFile.getAbsolutePath());
		} else {
			Settings.setFromJSON(JsonFile.getJSON(settingFile.getAbsolutePath()));
		}
	}

    public static Map<Integer, JSONObject> getResources(String folderName) {
        Logger.i(LOG_TAG, "Loading resources from folder '"+ folderName + "'" );
        Map<Integer, JSONObject> resources = new HashMap<>();
        File f = new File(Settings.getHomeFile(), folderName);
        if (!f.exists()) {
            Logger.d(LOG_TAG, "Folder " + folderName + " was not found.");
            return resources;
        }
        File resourcesJsonFiles[] = f.listFiles(fileFilter(FileFilterType.JSON));
        if (resourcesJsonFiles == null) {
            Logger.i(LOG_TAG, "No JSON resources found in " + f.getAbsolutePath());
            return resources;
        }
        for (File jsonFile : resourcesJsonFiles) {
            JSONObject json = JsonFile.getJSON(jsonFile.getAbsolutePath());
			int key = 0;
			String name = jsonFile.getName();
			try {
				key = Integer.valueOf(name.replace(".json", ""));
			} catch (Exception e) {
				Logger.e(LOG_TAG, "Error with parsing file name " + name);
				Logger.exception(LOG_TAG, e);
			}
			Logger.i(LOG_TAG, Integer.toString(key));
			if (key != 0) resources.put(key, json);
        }
        return resources;
    }

	/**
	 * Returns a FileFilter that filters out file that are not of the set file type.
	 * @param type of file wanted.
	 * @return FileFilter depending on what the type was.
	 */
	private static FileFilter fileFilter(FileFilterType type) {
		switch (type) {
		case FOLDERS:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
		case RECORDING_3GP:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().endsWith(".3gp")) && !pathname.isDirectory();
				}
			};
		case TEXT:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().endsWith(".txt")) && !pathname.isDirectory();
				}
			};
		case JSON:
			return new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return ((pathname.getName().endsWith(".json")) && !pathname.isDirectory());
				}
			};
		default:
			Logger.e(LOG_TAG, "File filter of type has not been set, type: " + type);
			return null;
		}
	}
}
