package com.thecacophonytrust.cacophonometer.videoRecording;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thecacophonytrust.cacophonometer.util.CameraUtil;
import com.thecacophonytrust.cacophonometer.util.Logger;

public class VideoPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = "CameraPreview.java";
    private SurfaceHolder mHolder;
    private VideoCaptureRunnable vcr = null;


    public VideoPreview(Context context, VideoCaptureRunnable vcr) {
        super(context);
        this.vcr = vcr;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //Surface created, tell camera where to draw.
        try {
            CameraUtil.getCamera().setPreviewDisplay(holder);
            CameraUtil.getCamera().startPreview();
            Logger.d(LOG_TAG, "Finished camera preview.");
            if (vcr != null)
                vcr.videoPreviewFinished(holder);

        } catch (Exception e) {
            Logger.e(LOG_TAG, "Error with setting display for camera.");
            Logger.exception(LOG_TAG, e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtil.releaseCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }
}
