package com.thecacophonytrust.cacophonometer.util;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.http.UploadManager;
import com.thecacophonytrust.cacophonometer.recording.RecordingManager;

public class Update {
    private static final String LOG_TAG = "Update.java";

    public static void now(){
        Log.i(LOG_TAG, "Updating device.");
        RecordingManager.update();
        UploadManager.update();
    }
}
