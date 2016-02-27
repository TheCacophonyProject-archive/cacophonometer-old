package com.thecacophonytrust.cacophonometer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.util.CameraUtil;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoCaptureRunnable;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoPreview;

public class CameraPreviewActivity extends Activity {
    private static final String LOG_TAG = "CameraPreviewA.java";

    private static VideoCaptureRunnable vcr = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "Camera preview started.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        if (CameraUtil.getCamera() == null) {
            Toast.makeText(getApplicationContext(), "Camera not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        CameraUtil.setDisplayOrientation(90);
        VideoPreview videoPreview = new VideoPreview(this, vcr);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(videoPreview);
        if (vcr != null) {
            vcr.previewSetupFinished(this);
            vcr = null;
        }
    }

    /**
     * Releases the camera then calls the super.onBackPressed()
     */
    @Override
    public void onBackPressed(){
        CameraUtil.releaseCamera();
        super.onBackPressed();
    }

    /**
     * Sets the VideoCaptureRunnable that will be told when the setup in finished.
     * This is to allow the VideoCaptureRunnable to wait until the conditions are ready to start recording.
     * @param vcr   The VideoCaptureRunnable
     */
    public static void setSetupCallback(VideoCaptureRunnable vcr) {
        CameraPreviewActivity.vcr = vcr;
    }
}
