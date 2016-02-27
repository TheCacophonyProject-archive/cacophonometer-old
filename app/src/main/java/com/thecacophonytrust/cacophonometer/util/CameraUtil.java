package com.thecacophonytrust.cacophonometer.util;

import android.hardware.Camera;

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
