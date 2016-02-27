package com.thecacophonytrust.cacophonometer.videoRecording;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.ThreadExecutor;

public class VideoCaptureService extends IntentService {
    private static final String LOG_TAG = "VideoCaptureServic.java";


    private static PowerManager.WakeLock wl = null;
    private static PowerManager pm = null;
    private static ThreadExecutor threadExecutor = new ThreadExecutor();
    private static final String PM_TAG = "VideoCaptureServiceWakeLock";
    private static VideoCaptureRunnable acr = null;
    private static int rule = 0;

    public VideoCaptureService() {
        super("VideoCaptureService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Logger.i(LOG_TAG, "Video capture Service started.");
        if (pm == null)
            pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PM_TAG);
        wl.acquire();

        //check that previous VideoCaptureRunnable finished
        if (acr != null && !acr.isFinished()){
            Logger.e(LOG_TAG, "A previous VideoCaptureRunnable had not called the VideoCaptureFinished yet.");
            wl.release();
            return;
        }

        //VideoSetup vs = new VideoSetup();
        //vs.start(VideoRules.getRuleDO(rule));
        acr = new VideoCaptureRunnable();
        acr.setRuleKey(rule);
        //Toast.makeText(MainActivity.getContext(), "Starting video capture.", Toast.LENGTH_SHORT).show();
        threadExecutor.execute(acr);
    }

    public static void finishedVideoCapture(boolean error) {
        Logger.d(LOG_TAG, "Releasing WL");
        wl.release();
        if (error)
            Toast.makeText(MainActivity.getContext(), "Error with video capture.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.getContext(), "Finished video capture.", Toast.LENGTH_SHORT).show();
    }

    public static void setRule(int rule){
        VideoCaptureService.rule = rule;
    }
}
