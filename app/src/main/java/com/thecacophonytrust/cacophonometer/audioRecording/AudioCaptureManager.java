package com.thecacophonytrust.cacophonometer.audioRecording;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.thecacophonytrust.cacophonometer.resources.Rule;
import com.thecacophonytrust.cacophonometer.util.Logger;

import java.util.Calendar;

/**
 * This class manages the recording process.
 * It sets an alarm for when a recording should start using the AlarmManager.
 * This alarm calls the AudioCaptureService that will then start a recording.
 * When the recording is complete the recordingFinished method is called.
 */
public class AudioCaptureManager {
    private static final String LOG_TAG = "AudioCaptureManager.java";

    private static boolean initialized = false;
    private static int nextRuleKey = 0;
    private static Calendar startTime = null;
    private static Calendar startTimeCalendar = null;
    private static Context context = null;
    private static AlarmManager am = null;
    private static PendingIntent pi = null;
    private static boolean recording = false;

    /**
     * This updated the recording manager. Checks that the correct alarm and rule is set.
     */
    public static void update(){
        Logger.d(LOG_TAG, "Updating AudioCaptureManager");
        if (needToUpdateRecordingAlarm()){
            updateRecordingAlarm();
        }
    }

    /**
     * Initializes the Recording Manager
     * @param context that will be used for displaying Toasts and AlarmManager
     */
    public static void init(Context context){
        if (initialized){
            Logger.e(LOG_TAG, "Calling init on AudioCaptureManager when is has already been initialized.");
            return;
        }
        AudioCaptureManager.context = context;
        initialized = true;
        Logger.i(LOG_TAG, "Initializing AudioCaptureManager.");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        update();
    }

    public static void setRecording(boolean recording) {
        AudioCaptureManager.recording = recording;
    }

    public static boolean isRecording() {
        return recording;
    }

    public static Calendar getStartTime(){
        return startTime;
    }

    private static boolean needToUpdateRecordingAlarm(){
        Logger.i(LOG_TAG, "Checking if Recording alarm needs updating.");
        Calendar now = Calendar.getInstance();
        int newNextRuleKey = Rule.getNextRuleKey();
        boolean result;
        if (nextRuleKey != newNextRuleKey) {
            Logger.d(LOG_TAG, "nextRuleKey does not equal Rule.getNextRuleKey.");
            result = true;
        } else if (now.after(startTimeCalendar)) {
            Logger.d(LOG_TAG, "Recording time has passed.");
            result = true;
        } else if (Rule.getFromKey(nextRuleKey) == null && nextRuleKey != 0) {
            Logger.d(LOG_TAG, "Next rule key was not found.");
            result = true;
        } else {
            if (newNextRuleKey == 0) return false;
            Calendar newStartTime = Rule.getRuleDO(newNextRuleKey).nextRecordingTime();
            if (newStartTime.equals(startTimeCalendar)) {
                Logger.d(LOG_TAG, "New start time equals saved start time.");
                result = false;
            } else {
                Logger.d(LOG_TAG, "New start time does not equal saved start time.");
                result = true;
            }
        }
        if (result) {
            Logger.i(LOG_TAG, "Recording alarm needs updating.");
        } else {
            Logger.i(LOG_TAG, "Recording alarm does not need updating.");
        }
        return result;
    }

    private static void updateRecordingAlarm(){
        Logger.i(LOG_TAG, "Updating recording alarm.");
        nextRuleKey = Rule.getNextRuleKey();
        if (nextRuleKey == 0) {
            Logger.d(LOG_TAG, "No rule found for recording. Canceling alarm.");
            am.cancel(pi);
            return;
        }
        if (pi != null) {
            am.cancel(pi);
            pi = null;
        }
        startTime = Rule.getRuleDO(nextRuleKey).nextRecordingTime();
        Calendar now = Calendar.getInstance();
        long secondsToRecording = (startTime.getTimeInMillis() - now.getTimeInMillis())/1000;
        Logger.d(LOG_TAG, "Seconds to recording: " + secondsToRecording);
        Logger.d(LOG_TAG, "Setting start recording alarm for rule " + nextRuleKey);
        Intent intent = new Intent(context, AudioCaptureService.class);
        pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), pi);
        AudioCaptureService.setRule(nextRuleKey);
    }
}
