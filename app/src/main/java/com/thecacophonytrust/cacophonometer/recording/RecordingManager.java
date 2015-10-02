package com.thecacophonytrust.cacophonometer.recording;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;
import com.thecacophonytrust.cacophonometer.util.AudioCaptureService;
import com.thecacophonytrust.cacophonometer.util.JSONFile;

/**
 * This class manages the recording process.
 * It sets an alarm for when a recording should start using the AlarmManager.
 * This alarm calls the AudioCaptureService that will then start a recording.
 * When the recording is complete the recordingFinished method is called.
 */
public class RecordingManager {
    private static final String LOG_TAG = "RecordingManager.java";

    private static boolean initialized = false;
    private static Rule nextRuleToRecord = null;
    private static long recordingStartTime = 0;
    private static Context context = null;
    private static AlarmManager am = null;
    private static PendingIntent pi = null;
    private static RecordingDataObject lastRdo = null;
    private static boolean recordingNow = false;

    /**
     * This updated the recording manager. Checks that the correct alarm and rule is set.
     */
    public static void update(){
        Log.d(LOG_TAG, "Updating RecordingManager");
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
            Log.e(LOG_TAG, "Calling init on RecordingManager when is has already been initialized.");
            return;
        }
        RecordingManager.context = context;
        initialized = true;
        Log.i(LOG_TAG, "Initializing RecordingManager.");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        update();
    }

    /**
     * This is called by the AudioCaptureRunnable when the recording is complete.
     * @param rdo the RecordingDataObject of the finished recording.
     * @param error boolean of if there was an error or not.
     */
    public static void recordingFinished(RecordingDataObject rdo, boolean error){
        if (error){
            Log.e(LOG_TAG, "There was an error with a recording.");
        } else {
            RecordingUploadManager.addRDO(rdo);
            JSONFile.saveJSONObject(Settings.getRecordingsFolder() + "/" + rdo.getJSONFileName(), rdo.asJSONObject());
            lastRdo = rdo;
        }
        update();
        AudioCaptureService.releaseWakeLock();
    }

    public static RecordingDataObject getLastRdo(){
        return lastRdo;
    }

    public static boolean isRecordingNow() {
        return recordingNow;
    }

    public static void setRecordingNow(boolean recordingNow) {
        RecordingManager.recordingNow = recordingNow;
    }

    public static long getRecordingStartTime(){
        return recordingStartTime;
    }

    private static boolean needToUpdateRecordingAlarm(){
        long newRecordingStartTime;
        Rule newRule = RulesArray.getNextRule();
        if (newRule == null){
            return false;
        }
        newRecordingStartTime = newRule.nextRecordingTime().getTimeInMillis();
        if (newRule != nextRuleToRecord || newRecordingStartTime != recordingStartTime){
            nextRuleToRecord = newRule;
            recordingStartTime = newRecordingStartTime;
            Log.v(LOG_TAG, "Recording alarm didn't need updating.");
            return true;
        } else {
            Log.v(LOG_TAG, "Recording alarm needs updating.");
            return false;
        }
    }

    private static void updateRecordingAlarm(){
        Log.i(LOG_TAG, "Updating recording alarm.");
        if (pi != null) {
            am.cancel(pi);
            pi = null;
        }
        Log.d(LOG_TAG, "Setting start recording alarm for rule " + nextRuleToRecord);
        Intent intent = new Intent(context, AudioCaptureService.class);
        pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, recordingStartTime, pi);
        AudioCaptureService.setRule(nextRuleToRecord);
    }
}
