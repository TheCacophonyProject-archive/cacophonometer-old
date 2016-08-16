package com.thecacophonytrust.cacophonometer.videoRecording;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.util.Logger;

import java.util.Calendar;

/**
 * This class was used to record video. But is no longer used/maintained.
 * We use to have an Arduino that had a PIR connected to it (motion sensor) and when the PIR detect
 * motion the Arduino would connect to the Android phone telling it to start recording a video.
 * This class is no longer used as we are using a Raspberry Pi for video recording and other
 * experimental ideas.
 */
public class VideoCaptureManager {
    private static final String LOG_TAG = "VideoCaptureManager.java";

    private static boolean initialized = false;
    private static int nextRuleKey = 0;
    private static Calendar startTime = null;
    private static Calendar startTimeCalendar = null;
    private static Context context = null;
    private static AlarmManager am = null;
    private static PendingIntent pi = null;
    private static boolean recording = false;
    private static Camera camera = null;

    /**
     * This updated the video capture manager. Checks that the correct alarm and rule is set.
     */
    public static void update(){
        Logger.d(LOG_TAG, "Updating VideoCaptureManager");
        if (needToUpdateVideoCaptureAlarm()){
            updateVideoCaptureAlarm();
        }
    }

    /**
     * Initializes the Video capture Manager
     * @param context that will be used for displaying Toasts and AlarmManager
     */
    public static void init(Context context){
        if (initialized){
            Logger.e(LOG_TAG, "Calling init on VideoCaptureManager when is has already been initialized.");
            return;
        }
        VideoCaptureManager.context = context;
        initialized = true;
        Logger.i(LOG_TAG, "Initializing VideoCaptureManager.");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        update();
    }

    public static Calendar getStartTime(){
        return startTime;
    }

    public static boolean isRecording() {
        return recording;
    }

    private static boolean needToUpdateVideoCaptureAlarm(){
        Logger.i(LOG_TAG, "Checking if Video capture alarm needs updating.");
        Calendar now = Calendar.getInstance();
        int newNextRuleKey = VideoRules.getNextRuleKey();
        boolean result;
        if (nextRuleKey != newNextRuleKey) {
            Logger.d(LOG_TAG, "nextRuleKey does not equal AudioRules.getNextRuleKey.");
            result = true;
        } else if (now.after(startTimeCalendar)) {
            Logger.d(LOG_TAG, "Video capture time has passed.");
            result = true;
        } else if (VideoRules.getFromKey(nextRuleKey) == null && nextRuleKey != 0) {
            Logger.d(LOG_TAG, "Next rule key was not found.");
            result = true;
        } else {
            if (newNextRuleKey == 0) return false;
            Calendar newStartTime = VideoRules.getRuleDO(newNextRuleKey).nextVideoCaptureTime();
            if (newStartTime.equals(startTimeCalendar)) {
                Logger.d(LOG_TAG, "New start time equals saved start time.");
                result = false;
            } else {
                Logger.d(LOG_TAG, "New start time does not equal saved start time.");
                result = true;
            }
        }
        if (result) {
            Logger.i(LOG_TAG, "Video capture alarm needs updating.");
        } else {
            Logger.i(LOG_TAG, "Video capture alarm does not need updating.");
        }
        return result;
    }

    private static void updateVideoCaptureAlarm(){
        Logger.i(LOG_TAG, "Updating video capture alarm.");
        nextRuleKey = VideoRules.getNextRuleKey();
        if (nextRuleKey == 0) {
            Logger.d(LOG_TAG, "No rule found for video capture. Canceling Video capture alarm.");
            am.cancel(pi);
            return;
        }
        if (pi != null) {
            am.cancel(pi);
            pi = null;
        }
        startTime = VideoRules.getRuleDO(nextRuleKey).nextVideoCaptureTime();
        Calendar now = Calendar.getInstance();
        long secondsToVideoCapture = (startTime.getTimeInMillis() - now.getTimeInMillis())/1000;
        Logger.d(LOG_TAG, "Seconds to video capture: " + secondsToVideoCapture);
        Logger.d(LOG_TAG, "Setting start video capture alarm for rule " + nextRuleKey);
        Intent intent = new Intent(context, VideoCaptureService.class);
        pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), pi);
        VideoCaptureService.setRule(nextRuleKey);
    }

    public static void setRecording(boolean recording) {
        VideoCaptureManager.recording = recording;
    }
}
