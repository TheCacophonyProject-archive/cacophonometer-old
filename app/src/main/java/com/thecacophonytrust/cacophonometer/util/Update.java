package com.thecacophonytrust.cacophonometer.util;

import com.thecacophonytrust.cacophonometer.audioRecording.AudioCaptureManager;
import com.thecacophonytrust.cacophonometer.http.UploadManager;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoCaptureManager;

public class Update {
    private static final String LOG_TAG = "Update.java";

    public static void now(){
        Logger.i(LOG_TAG, "Updating device.");
        AudioCaptureManager.update();
        VideoCaptureManager.update();
        UploadManager.update();

    }
}
