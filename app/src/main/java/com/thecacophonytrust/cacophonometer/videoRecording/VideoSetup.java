package com.thecacophonytrust.cacophonometer.videoRecording;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.util.CameraUtil;
import com.thecacophonytrust.cacophonometer.util.Logger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class was used to record video. But is no longer used/maintained.
 * We use to have an Arduino that had a PIR connected to it (motion sensor) and when the PIR detect
 * motion the Arduino would connect to the Android phone telling it to start recording a video.
 * This class is no longer used as we are using a Raspberry Pi for video recording and other
 * experimental ideas.
 */
public class VideoSetup {
    private static final String LOG_TAG = "VideoSetup.java";

    private static DateFormat iso8601 = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.UK); //This format is used as it has it's characters are all file friendlily (not including -:+....)

    private VideoRules.DataObject rule = null;
    private MediaRecorder mRecorder = null;
    private String filePath = null;

    private boolean vpFinished = false;
    private boolean cpFinished = false;
    private SurfaceHolder vp = null;

    public void start(VideoRules.DataObject rule){
        Logger.i(LOG_TAG, "Starting video capture setup.");
        this.rule = rule;

        Date date = new Date(System.currentTimeMillis());
        String fileName = iso8601.format(date)+".mp4";
        File file = new File(Settings.getRecordingsFolder(), fileName);
        this.filePath = file.getAbsolutePath();

        //CameraPreviewActivity.setSetupCallback(this);

        MainActivity.currentInstance.openCameraPreview();

    }

    public void previewSetupFinished(){
        this.cpFinished = true;
        if (this.vpFinished) {
            initRecording();
        }
    }

    public void videoPreviewFinished(SurfaceHolder vp){
        this.vp = vp;
        this.vpFinished = true;
        if (this.cpFinished) {
            initRecording();
        }
    }

    public void initRecording(){
        Logger.d(LOG_TAG, "Preparing video capture.");
        CameraUtil.getCamera().unlock();
        mRecorder = new MediaRecorder();
        mRecorder.setCamera(CameraUtil.getCamera());
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setProfile(CamcorderProfile.get(MediaRecorder.OutputFormat.MPEG_4));
        mRecorder.setOutputFile(filePath);
        mRecorder.setPreviewDisplay(vp.getSurface());
        Logger.d(LOG_TAG, "6");
        try {
            mRecorder.prepare();
            Thread.sleep(3000);
            Logger.d(LOG_TAG, "Finished preparing video capture.");
            mRecorder.start();
            Thread.sleep(5000);
            mRecorder.stop();
            Logger.i(LOG_TAG, "Finished video capture");
        } catch (Exception e) {
            Logger.e(LOG_TAG, "Error with video recording.");
            Logger.exception(LOG_TAG, e);
        }
    }

}
