package com.thecacophonytrust.cacophonometer.audioRecording;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.ThreadExecutor;

public class AudioCaptureService extends IntentService{
    private static final String LOG_TAG = "AudioCaptureServic.java";


    private static PowerManager.WakeLock wl = null;
    private static PowerManager pm = null;
    private static ThreadExecutor threadExecutor = new ThreadExecutor();
    private static final String PM_TAG = "AudioCaptureServiceWakeLock";
    private static AudioCaptureRunnable acr = null;
    private static int rule = 0;

    public AudioCaptureService() {
        super("AudioCaptureService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        if (pm == null)
            pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PM_TAG);
        wl.acquire();

        //check that previous AudioCaptureRunnable finished
        if (acr != null && !acr.isFinished()){
            Logger.e(LOG_TAG, "A previous AudioCaptureRunnable had not called the AudioCaptureFinished yet.");
            wl.release();
            return;
        }
        acr = new AudioCaptureRunnable();
        acr.setRuleKey(rule);
        Toast.makeText(MainActivity.getContext(), "Starting audio capture.", Toast.LENGTH_SHORT).show();
        threadExecutor.execute(acr);
    }

    public static void finishedAudioCapture(boolean error) {
        Logger.d(LOG_TAG, "Releasing WL");
        wl.release();
        if (error)
            Toast.makeText(MainActivity.getContext(), "Error with audio capture.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.getContext(), "Finished audio capture.", Toast.LENGTH_SHORT).show();
    }

    public static void setRule(int rule){
        AudioCaptureService.rule = rule;
    }
}
