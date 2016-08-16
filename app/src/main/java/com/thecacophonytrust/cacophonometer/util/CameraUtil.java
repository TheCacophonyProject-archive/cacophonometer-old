package com.thecacophonytrust.cacophonometer.util;

import android.hardware.Camera;

/**
 * This class was used to record video recordings. But is no longer used/maintained.
 * We use to have an Arduino that had a PIR connected to it (motion sensor) and when the PIR detect
 * motion the Arduino would connect to the Android phone telling it to start recording a video.
 * This class is no longer used as we are using a Raspberry Pi for video recording and other
 * experimental ideas.
 */
public class CameraUtil {
    private static final String LOG_TAG = "CameraUtil.java";
    private static Camera camera = null;

    public static Camera getCamera() {
        try {
            if (camera == null) {
                camera = Camera.open();
            }
            return camera;
        } catch (Exception e) {
            Logger.e(LOG_TAG, "Error with getting open camera.");
            Logger.exception(LOG_TAG, e);
            return null;
        }
    }

    public static void setDisplayOrientation(int deg) {
        if (camera != null) {
            camera.setDisplayOrientation(deg);
        } else {
            Logger.w(LOG_TAG, "No camera found to set orientation.");
        }
    }

    static public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        } else {
            Logger.w(LOG_TAG, "releaseCamera was called when camera was null.");
        }
    }
}
