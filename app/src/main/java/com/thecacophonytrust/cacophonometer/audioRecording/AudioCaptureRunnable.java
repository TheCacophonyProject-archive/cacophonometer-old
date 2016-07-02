package com.thecacophonytrust.cacophonometer.audioRecording;

import android.media.MediaRecorder;
import android.os.Looper;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.resources.AudioFile;
import com.thecacophonytrust.cacophonometer.resources.AudioRecording;
import com.thecacophonytrust.cacophonometer.resources.AudioRules;
import com.thecacophonytrust.cacophonometer.resources.Hardware;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.Software;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioCaptureRunnable implements Runnable{

    private static final String LOG_TAG = "AudioCaptureRunnab.java";

    private static DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    private static DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.UK);
    private static DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.UK);
    private AudioRules.DataObject rule = null;
    private MediaRecorder mRecorder = null;
    private boolean finished = false;

    @Override
    public void run() {
        Logger.i(LOG_TAG, "Starting audio capture runnable.");
        AudioCaptureManager.setRecording(true);
        Looper.prepare();
        Location.getNewLocation();
        finished = false;

        Date date = new Date(System.currentTimeMillis());
        String fileName = fileFormat.format(date)+".3gp";
        File file = new File(Settings.getRecordingsFolder(), fileName);
        String filePath = file.getAbsolutePath();

        long sleepTime = rule.getDuration() * 1000;
        JSONObject audioRecording = new JSONObject();
        JSONObject audioFile = new JSONObject();

        boolean error = false;
        try {
            mRecorder = new MediaRecorder();
            prepareMediaRecorder(filePath);

            mRecorder.start();
            Thread.sleep(sleepTime);
            mRecorder.stop();



            audioFile.put("localFilePath", filePath);
            audioFile.put("duration", rule.getDuration());
            audioFile.put("startTimestamp", dateFormatTime.format(date));
            audioFile.put("recordingDateTime", dateFormatDateTime.format(date));
            audioRecording.put("audioFileKey", AudioFile.addAndSave(audioFile));
            audioRecording.put("locationKey", Location.getMostRecentKey());
            audioRecording.put("hardwareKey", Hardware.getCurrentKey());
            audioRecording.put("softwareKey", Software.getCurrentKey());
            audioRecording.put("batteryPercentage", MainActivity.getBatteryPercentage());
            audioRecording.put("uploaded", false);

            AudioRecording.add(audioRecording);
        } catch (JSONException | InterruptedException | IOException e) {
            error = true;
            Logger.e(LOG_TAG, "Error with recording.");
            Logger.exception(LOG_TAG, e);
        } finally {
            finished = true;
            AudioCaptureService.finishedAudioCapture(error);
            Logger.i(LOG_TAG, "Finished audio capture runnable.");
            AudioCaptureManager.setRecording(false);
            Update.now();
        }
    }

    public boolean isFinished(){
        return finished;
    }

    public void setRuleKey(int key) {
        this.rule = AudioRules.getRuleDO(key);
    }

    private void prepareMediaRecorder(String filePath) throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
    }
}