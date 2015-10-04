package com.thecacophonytrust.cacophonometer.util;

import android.media.MediaRecorder;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.recording.RecordingDataObject;
import com.thecacophonytrust.cacophonometer.recording.RecordingManager;
import com.thecacophonytrust.cacophonometer.rules.Rule;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AudioCaptureRunnable implements Runnable{

    private static final String LOG_TAG = "AudioCaptureRunnab.java";

    private static String FILE_EXTENSION = "3gp";

    private Rule rule = null;
    private MediaRecorder mRecorder = null;
    private RecordingDataObject rdo = null;
    private boolean error = false;
    private boolean finished = false;

    @Override
    public void run() {
        Log.i(LOG_TAG, "Starting audio capture runnable.");
        finished = false;
        setRecordingDataObject();
        long sleepTime = rule.getDuration() * 1000;
        try {
            mRecorder = new MediaRecorder();
            prepareMediaRecorder();
            mRecorder.start();
            Thread.sleep(sleepTime);
            mRecorder.stop();

        } catch (InterruptedException | IOException e) {
            error = true;
            Log.e(LOG_TAG, "Error with recording.");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, sw.toString());
        }
        finished = true;
        Log.i(LOG_TAG, "Finished audio capture runnable.");
        RecordingManager.recordingFinished(rdo, error);
    }

    public boolean isFinished(){
        return finished;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    private void prepareMediaRecorder() throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(rdo.getRecordingFile().getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
    }

    private void setRecordingDataObject(){
        if (rule == null){
            Log.e(LOG_TAG, "Rule is null.");
        }
        rdo = new RecordingDataObject(rule.getName(), Settings.getDeviceId(), rule.getDuration(), System.currentTimeMillis(), 0, FILE_EXTENSION);
        rdo.setHardwareKey(JSONMetadata.getCurrentHardwareKey());
        rdo.setSoftwareKey(JSONMetadata.getCurrentSoftwareKey());
    }
}