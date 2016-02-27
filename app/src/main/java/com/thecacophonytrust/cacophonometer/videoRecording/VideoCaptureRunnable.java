package com.thecacophonytrust.cacophonometer.videoRecording;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Looper;
import android.view.SurfaceHolder;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.activity.CameraPreviewActivity;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.resources.AudioFile;
import com.thecacophonytrust.cacophonometer.resources.Hardware;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.Software;
import com.thecacophonytrust.cacophonometer.resources.VideoFile;
import com.thecacophonytrust.cacophonometer.resources.VideoRecording;
import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.util.CameraUtil;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoCaptureRunnable implements Runnable{
    private static final String LOG_TAG = "VideoCaptureRunnab.java";

    private static DateFormat iso8601 = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.UK); //This format is used as it has it's characters are all file friendlily (not including -:+....)

    private VideoRules.DataObject rule = null;
    private MediaRecorder mRecorder = null;
    private boolean vpFinished = false;
    private boolean cpFinished = false;
    private SurfaceHolder vp = null;
    private CameraPreviewActivity cpa = null;

    private boolean finished = false;

    private String filePath = null;

    @Override
    public void run() {
        Logger.i(LOG_TAG, "Starting video capture runnable.");
        Looper.prepare();

        CameraPreviewActivity.setSetupCallback(this);
        VideoCaptureManager.setRecording(true);

        Date date = new Date(System.currentTimeMillis());
        String fileName = iso8601.format(date)+".mp4";
        File file = new File(Settings.getRecordingsFolder(), fileName);
        filePath = file.getAbsolutePath();

        MainActivity.currentInstance.openCameraPreview();
    }

    public boolean isFinished(){
        return finished;
    }

    public void setRuleKey(int key) {
        this.rule = VideoRules.getRuleDO(key);
    }

    public void previewSetupFinished(CameraPreviewActivity cpa){
        this.cpa = cpa;
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

        JSONObject videoRecording = new JSONObject();
        JSONObject videoFile = new JSONObject();

        boolean error = false;
        try {
            mRecorder = new MediaRecorder();
            CameraUtil.getCamera().unlock();
            mRecorder.setCamera(CameraUtil.getCamera());
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mRecorder.setProfile(CamcorderProfile.get(MediaRecorder.OutputFormat.MPEG_4));
            mRecorder.setOutputFile(filePath);
            mRecorder.setPreviewDisplay(vp.getSurface());
            long sleepTime = rule.getDuration() * 1000;
            mRecorder.prepare();
            Thread.sleep(1000);
            Logger.d(LOG_TAG, "Finished preparing video capture.");
            mRecorder.start();
            Thread.sleep(sleepTime);
            mRecorder.stop();
            cpa.onBackPressed();

            videoFile.put("localFilePath", filePath);
            videoFile.put("duration", rule.getDuration());
            videoRecording.put("videoFileKey", VideoFile.addAndSave(videoFile));
            videoRecording.put("locationKey", Location.getMostRecentKey());
            videoRecording.put("hardwareKey", Hardware.getCurrentKey());
            videoRecording.put("softwareKey", Software.getCurrentKey());
            videoRecording.put("batteryPercentage", MainActivity.getBatteryPercentage());
            videoRecording.put("uploaded", false);

            VideoRecording.add(videoRecording);

            Logger.i(LOG_TAG, "Finished video capture");
        } catch (Exception e) {
            error = true;
            Logger.e(LOG_TAG, "Error with video recording.");
            Logger.exception(LOG_TAG, e);
        } finally {
            finished = true;
            VideoCaptureService.finishedVideoCapture(error);
            Logger.i(LOG_TAG, "Finished video capture runnable.");
            VideoCaptureManager.setRecording(false);
            Update.now();
        }
    }
}
