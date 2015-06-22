package com.thecacophonytrust.cacophonometer.recording;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;

public class RecordingAlarm extends BroadcastReceiver {

	private static final String LOG_TAG = "RecordingAlarm.java";
	private static Rule rule = null;
	private static long recordingStartTime = 0;
	private static long recordingStopTime = 0;
	private static AlarmManager am = null;
	private static PendingIntent pi = null;
	private static boolean isRecording = false;

	
	/**
	 * This method is called when the recording alarm goes off.
	 * It checks if the device is recording.
	 * If device is not recording it will start the process of recording and setting another alarm to stop the recording.
	 * If the device is recording it will start the process of stopping the recording and saving it.
	 */
	@Override
	public void onReceive(Context context, Intent i) {
		
		if (isRecording){
			//Device is recording, so stop recording.
			Log.i(LOG_TAG, "Alarm Received, stopping recording");
			Toast.makeText(context, "Stopped recording", Toast.LENGTH_SHORT).show();
			Recording.stopRecording();
			isRecording = false;
			MainActivity.getCurrent().onResume();
		} else {
			//Device is not recording, so start recording.
			Log.i(LOG_TAG, "Alarm Received, starting recording");
			Recording.startRecord(rule);
			Toast.makeText(context, "Started recording", Toast.LENGTH_SHORT).show();
			isRecording = true;
			Intent intent = new Intent(context, RecordingAlarm.class);
			pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			am.set(AlarmManager.RTC_WAKEUP, recordingStopTime, pi);
		}
	}


	/**
	 * Updates the alarm for the next rule.
	 * Finds what the next rule to be recorded should be and then changes
	 * the alarm to go off for that rule if is isn't already set for that rule and time.
	 * This is only done if the device si not recording.
	 * @param context
	 */
	public static void update(Context context){
		Rule newRule;
		long newRecordingStartTime;
		long newRecordingStopTime;
		
		newRule = RulesArray.getNextRule();
		if (newRule == null)
			return;
		
		newRecordingStartTime = newRule.nextRecordingTime().getTimeInMillis();
		newRecordingStopTime = newRecordingStartTime + newRule.getDuration()*1000;
		
		if (isRecording){
			Log.d(LOG_TAG, "Can't update recording alarm, recording in progress.");
		}
		else if (newRule != rule || newRecordingStartTime != recordingStartTime){
			rule = newRule;
			recordingStartTime = newRecordingStartTime;
			recordingStopTime = newRecordingStopTime;
			setAlarm(context);
		} else {
			Log.d(LOG_TAG, "Recording alarm is up to date.");
		}
	}
	
	public Rule getRule(){
		return rule;
	}


	/**
	 * Sets the alarm for the next rule using the recordingStartTime class variable.
	 * Replaces old alarms if they were set.
	 * @param context
	 */
	private static void setAlarm(Context context) {

		if (am == null)
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		if (pi != null) {
			am.cancel(pi);
			pi = null;
		}
		Log.d(LOG_TAG, "Setting start recording alarm for rule " + rule);
		Intent intent = new Intent(context, RecordingAlarm.class);
		pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, recordingStartTime, pi);

	}

	/**
	 * Returns the time in seconds until the next recording should start.
	 * If the device is recording 0 is returned.
	 * 
	 * @return time to next recording
	 */
	public static long timeUntilRecording(){
		if (isRecording) 
			return 0;
		
		long now = Calendar.getInstance().getTimeInMillis();
		long timeToNextRecording = recordingStartTime - now;

		return timeToNextRecording/1000;
	}
}