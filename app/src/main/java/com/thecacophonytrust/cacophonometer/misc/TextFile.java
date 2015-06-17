package com.thecacophonytrust.cacophonometer.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.enums.TextFileKeyType;

public class TextFile {

	/**
	 * This class deals with text files, saving and parsing.
	 */

	private static final String LOG_TAG = "ParseTextFile.java";
	
	private static final String REGEX_PATTERN = "([A-Z_]+)(:\\s)(.*)";	//Regex pattern to get data from a line in text file


	/**
	 * Returns a map containing the data of the text file.
	 * @param f file of text file to be parsed
	 * @return	data in text file in the form of a Map: Map<TextFileKeyType, String>
	 */
	public static Map<TextFileKeyType, String> parseTextFile(File f){
		Log.d(LOG_TAG, "Parsing file '"+f.getPath()+"'");
		Map<TextFileKeyType, String> map = new HashMap<TextFileKeyType, String>();
		String line;
		TextFileKeyType key;
		String value;
		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(isr);
			
			while ((line = br.readLine()) != null){
				key = TextFileKeyType.fromString(line.replaceAll(REGEX_PATTERN, "$1"));
				value = line.replaceAll(REGEX_PATTERN, "$3");
				if (value != null && !value.equals(""))
					map.put(key, value);
			}
		} catch (Exception e){
			Log.d(LOG_TAG, e.toString());
			return null;
		}
		return map;
	}

	/**
	 * Saves a text file with the given parameters.
	 * @param valueMap Map of data to be saved in text file
	 * @param dir	directory where the text file will be saved.
	 * @param name	name of text file to be saved.
	 * @return	true if file was save successfully.
	 */
	public static boolean saveTextFile(Map<TextFileKeyType, String> valueMap, File dir, String name){
		Log.d(LOG_TAG, "Saving text file at '"+dir.getPath() + "/"+name +"'");
		dir.mkdirs();
		File f = new File(dir, name);
		FileWriter fw;
		String content = "";
		String value;
		for (TextFileKeyType key : valueMap.keySet()){
			value = valueMap.get(key);
			content += String.format("%s: %s\n", key, value);
		}
		try {
			fw = new FileWriter(f, false);
			fw.write(content);
			fw.close();
		} catch (Exception e){
			Log.e(LOG_TAG, e.toString());
			return false;
		}
		return true;
	}
}
