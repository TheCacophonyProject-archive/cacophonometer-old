package com.thecacophonytrust.cacophonometer.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.thecacophonytrust.cacophonometer.rules.Rule;

public class AudioCaptureService extends IntentService{
    private static final String LOG_TAG = "AudioCaptureServic.java";


    private static PowerManager.WakeLock wl = null;
    private static PowerManager pm = null;
    private static ThreadExecutor threadExecutor = new ThreadExecutor();
    private static final String PM_TAG = "AudioCaptureServiceWakeLock";
    private static AudioCaptureRunnable acr = null;
    private static Rule rule = null;


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
            Log.e(LOG_TAG, "A previous AudioCaptureRunnable had not called the AudioCaptureFinished yet.");
            wl.release();
            return;
        }
        acr = new AudioCaptureRunnable();
        acr.setRule(rule);
        threadExecutor.execute(acr);
    }

    public static void releaseWakeLock(){
        wl.release();
    }

    public static void setRule(Rule rule){
        AudioCaptureService.rule = rule;
    }
}
