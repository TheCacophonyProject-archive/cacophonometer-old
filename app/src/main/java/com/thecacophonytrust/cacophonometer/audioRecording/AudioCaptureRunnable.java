package com.thecacophonytrust.cacophonometer.audioRecording;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.activity.MainActivity;
import com.thecacophonytrust.cacophonometer.resources.AudioFile;
import com.thecacophonytrust.cacophonometer.resources.AudioRecording;
import com.thecacophonytrust.cacophonometer.resources.AudioRules;
import com.thecacophonytrust.cacophonometer.resources.Hardware;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.Software;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioCaptureRunnable implements Runnable{

    private static final String LOG_TAG = "AudioCaptureRunnab.java";

    private static DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    private static DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.UK);
    private static DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.UK);
    private AudioRules.DataObject rule = null;
    private MediaRecorder mRecorder = null;
    private boolean finished = false;

    @Override
    public void run() {
        Logger.i(LOG_TAG, "Starting audio capture runnable.");
        AudioCaptureManager.setRecording(true);
        Looper.prepare();
        Location.getNewLocation();
        finished = false;

        Date date = new Date(System.currentTimeMillis());
        String fileName = fileFormat.format(date)+".3gp";
        File file = new File(Settings.getRecordingsFolder(), fileName);
        String filePath = file.getAbsolutePath();

        long recordTime = rule.getDuration() * 1000;
        JSONObject audioRecording = new JSONObject();
        JSONObject audioFile = new JSONObject();

        boolean error = false;
        try {
            mRecorder = new MediaRecorder();
            prepareMediaRecorder(filePath);

            // Give warning that recording is about to start
            //http://stackoverflow.com/questions/6462105/how-do-i-access-androids-default-beep-sound

            Context context = Settings.getContext();

            AudioManager am =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            am.setStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
            final ToneGenerator tg2 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

            // First beep before recording starts
        for (int i=0; i<10; i++) {
            tg2.startTone(ToneGenerator.TONE_DTMF_0, 100);
            Thread.sleep(2000);
        }
            MediaPlayer mp = new MediaPlayer();

            mp.setDataSource("/mnt/sdcard/yourdirectory/youraudiofile.mp3");
            mp.prepare();
            mp.start();


            int totalTime = 0;
            int beepTime = 50;
            int oneLoopTime = 2000;
            int recordingFinishedBeepTime = 5000;
            mRecorder.start();
           // now beep faster during recording
            while (totalTime < recordTime){
                tg2.startTone(ToneGenerator.TONE_DTMF_0, beepTime);
                Thread.sleep(oneLoopTime);
                totalTime = totalTime + oneLoopTime;
            }

            mRecorder.stop();



            tg2.startTone(ToneGenerator.TONE_DTMF_0, recordingFinishedBeepTime);
//            Thread.sleep(2000);
//
//            tg2.stopTone();

            audioRecording.put("duration", rule.getDuration());
            audioRecording.put("localFilePath", filePath);
            audioRecording.put("recordingDateTime", dateFormatDateTime.format(date));
            audioRecording.put("recordingTime", dateFormatTime.format(date));


            AudioRecording.add(audioRecording);
            rule.lastRecordingTime = date;           // Saves the last recording time to the audio rule data object.
        } catch (JSONException | InterruptedException | IOException e) {
            error = true;
            Logger.e(LOG_TAG, "Error with recording.");
            Logger.exception(LOG_TAG, e);
        } finally {
            finished = true;
            AudioCaptureService.finishedAudioCapture(error);
            Logger.i(LOG_TAG, "Finished audio capture runnable.");
            AudioCaptureManager.setRecording(false);
            Update.now();
        }
    }

    public boolean isFinished(){
        return finished;
    }

    public void setRuleKey(int key) {
        this.rule = AudioRules.getRuleDO(key);
    }

    private void prepareMediaRecorder(String filePath) throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
    }
}