package com.thecacophonytrust.cacophonometer.rules;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.enums.TextFileKeyType;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.TextFile;

public class Rule {

	/**
	 * Rules.class holds all relevant data for a rule.
	 */

	private static final String LOG_TAG = "Rule.java";
	
	private String name = null;
	private int duration = -1; 					// Length in seconds
	private int startTimeHour = 0;				
	private int startTimeMinute = 0;

	public Rule(){
		
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getStartTimeHour() {
		return startTimeHour;
	}
	
	public int getStartTimeMinute() {
		return startTimeMinute;
	}

	public void setStartTimeMinute(int startTimeMinute) {
		this.startTimeMinute = startTimeMinute;
	}

	public void setStartTimeHour(int startTimeHour) {
		this.startTimeHour = startTimeHour;
	}

	/**
	 * Finds the next recording time in UTC.
	 * @return next recording time in UTC.
	 */
	public Calendar nextRecordingTime(){
		Calendar recordingTime = Calendar.getInstance();
		recordingTime.set(Calendar.HOUR_OF_DAY, startTimeHour);
		recordingTime.set(Calendar.MINUTE, startTimeMinute);
		recordingTime.set(Calendar.SECOND, 0);
		recordingTime.set(Calendar.MILLISECOND, 0);
		//TODO check what happens on a new year
		while (recordingTime.before(Calendar.getInstance())){
			recordingTime.add(Calendar.DAY_OF_YEAR, 1);
		}
		return recordingTime;
	}
	
	@Override
	public String toString() {
		return "Name: " + name + ", Start: " + getTimeAsString();
	}

	/**
	 * Checks if the recording is valid.
	 * @return true if recording is valid, false if not.
	 */
	public boolean isValid() {
		boolean result = true;
		if (getName() == null){
			result = false;
			Log.d(LOG_TAG, "No name found");
		}
		if (getDuration() < 0){
			result = false;
			Log.d(LOG_TAG, "Duration is invalid: " + duration);
		}
		
		//TODO add checks 
		return result;
	}


	/**
	 * Compares given rule with its own values to see if the rules are equivalent.
	 * @param rule to compare to.
	 * @return true if rules are equal/equivalent.
	 */
	public boolean equal(Rule rule){
		if (rule.getDuration() != getDuration())
			return false;
		if (!rule.getName().equals(getName()))
			return false;
		if (rule.getStartTimeHour() != getStartTimeHour())
			return false;
		if (rule.getStartTimeMinute() != getStartTimeMinute())
			return false;
		return true;
	}

	/**
	 * Saves a rule to a text file.
	 * @return true if rule was saved successfully
	 */
	public boolean save(){
		Map<TextFileKeyType, String> valueMap = new HashMap<>();
		
		valueMap.put(TextFileKeyType.NAME, getName());
		valueMap.put(TextFileKeyType.START_TIME_HOUR, Integer.toString(getStartTimeHour()));
		valueMap.put(TextFileKeyType.START_TIME_MINUTE, Integer.toString(getStartTimeMinute()));
		valueMap.put(TextFileKeyType.DURATION, Integer.toString(getDuration()));
		
		return TextFile.saveTextFile(valueMap, Settings.getRulesFolder(), getTextFileName());
	}

	/**
	 * Returns the name of the text file of the rule.
	 * @return name of rule text file.
	 */
	public String getTextFileName(){
		return name + ".txt";
	}

	/**
	 * Deletes the rule text file and remove rule from rule array in RulesArray.class
	 * @return true if file was deleted, false if not.
	 */
	public boolean delete(){
		boolean result;
		RulesArray.removeRule(this);
		File ruleFile = new File(Settings.getRulesFolder(), getTextFileName());
		if (ruleFile.exists()){
			result = ruleFile.delete();
		} else {
			Log.d(LOG_TAG, "Rule text file not found to delete");
			result = false;
		}
		return result;
	}


	/**
	 * Returns the time of recording as a readable string.
	 * @return time of recording.
	 */
	public String getTimeAsString() {
		String minuteText;
		String hourText;
		if (getStartTimeMinute() < 10)
			minuteText = "0" + Integer.toString(getStartTimeMinute());
		else
			minuteText = Integer.toString(getStartTimeMinute());
		if (getStartTimeHour() < 10)
			hourText = "0" + Integer.toString(getStartTimeHour());
		else 
			hourText = Integer.toString(getStartTimeHour());
		return hourText+":"+minuteText;
	}
}
