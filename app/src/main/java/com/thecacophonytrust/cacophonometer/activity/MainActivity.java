package com.thecacophonytrust.cacophonometer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.audioRecording.AudioCaptureManager;
import com.thecacophonytrust.cacophonometer.resources.AudioRules;
import com.thecacophonytrust.cacophonometer.resources.Hardware;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.Software;
import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.util.GPS;
import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoCaptureManager;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity.java";
    private static Context context = null;

    public static MainActivity currentInstance = null;

    @Override
    public void onBackPressed() {
        //TODO make exit if pressed twice in a row
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(LOG_TAG, "MainActivity created.");
        init();
        currentInstance = this;
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Logger.d(LOG_TAG, "Resuming main activity");
        Update.now();
        updateText();   //Updates the text displayed on the screen for MainActivity
        displayNextRecordingStatus();
        super.onResume();
    }

    /**
     * Displays a Toast of the recording status (time until recording, device is recording, no recording found).
     */
    private void displayNextRecordingStatus(){
        String audioText;
        if (AudioCaptureManager.isRecording())
            audioText = "Device is audio recording now.";
        else if (AudioRules.getNextRuleKey() == 0)
            audioText = "No audio rules found for recording";
        else {
            //TODO this could be done better... but works for now.
            Calendar timeOfRecording = AudioCaptureManager.getStartTime();

            if (timeOfRecording != null) {
                long timeToNextRecording = (timeOfRecording.getTimeInMillis() - System.currentTimeMillis())/1000;
                int hours = (int) (timeToNextRecording / 3600);
                int minutes = (int) (timeToNextRecording % 3600) / 60;
                int seconds = (int) timeToNextRecording % 60;
                audioText = String.format("Next audio recording starts in %02d:%02d:%02d.", hours, minutes, seconds);
            } else {
                audioText = "No rules found for recording.";
            }
        }
        Toast.makeText(getApplicationContext(), audioText, Toast.LENGTH_SHORT).show();

        String videoText;
        if (VideoCaptureManager.isRecording())
            videoText = "Device is video recording now.";
        else if (VideoRules.getNextRuleKey() == 0)
            videoText = "No video rules found for recording";
        else {
            //TODO this could be done better... but works for now.
            Calendar timeOfRecording = VideoCaptureManager.getStartTime();

            if (timeOfRecording != null) {
                long timeToNextRecording = (timeOfRecording.getTimeInMillis() - System.currentTimeMillis())/1000;
                int hours = (int) (timeToNextRecording / 3600);
                int minutes = (int) (timeToNextRecording % 3600) / 60;
                int seconds = (int) timeToNextRecording % 60;
                videoText = String.format("Next video recording starts in %02d:%02d:%02d.", hours, minutes, seconds);
            } else {
                videoText = "No rules found for video recording.";
            }
        }
        Toast.makeText(getApplicationContext(), videoText, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the text that is displayed.
     */
    private void updateText() {
        int nextAudioRuleKey = AudioRules.getNextRuleKey();
        TextView audioInfoTextView = (TextView) findViewById(R.id.next_audio_recording_text);

        if (nextAudioRuleKey == 0){
            audioInfoTextView.setText("No rule found for next audio recording");
        } else {
            //TODO
            int hour = AudioRules.getRuleDO(nextAudioRuleKey).nextAlarmTime().get(Calendar.HOUR_OF_DAY);
            int minute = AudioRules.getRuleDO(nextAudioRuleKey).nextAlarmTime().get(Calendar.MINUTE);
            String name = AudioRules.getRuleDO(nextAudioRuleKey).name;
            audioInfoTextView.setText(String.format("Next audio recording at %02d:%02d for rule '%s'", hour, minute, name));
        }

    }

    /**
     * Opens the Settings activity
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * Opens the camera preview activity
     */
    public void openCameraPreview(View v) {
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);
    }

    public void openCameraPreview() {
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the Rules activity
     */
    public void openAudioRules(View v) {
        Intent intent = new Intent(this, AudioRulesMenuActivity.class);
        startActivity(intent);
    }

    public void openVideoRules(View v) {
        Intent intent = new Intent(this, VideoRulesMenuActivity.class);
        startActivity(intent);
    }


    /**
     * Plays the last recording that was recorded in this session.
     * @param v
     */
    public void playLastRecording(View v) {
        //TODO
    }

    public void init() {
        Logger.i(LOG_TAG, "Initializing Cacophonometer.");
        SharedPreferences prefs = getSharedPreferences(Settings.PREFS_NAME, 0);
        if (prefs.getBoolean("first_time", true)) {
            AudioRules.addDefaultRules();
            prefs.edit().putBoolean(Settings.PREFS_NAME, false);
        }

        context = getApplicationContext();
        Settings.setFromJSON(JsonFile.getJSON(Settings.getSettingsFile().getAbsolutePath()));
        Location.loadFromFile();
        Hardware.loadFromFile();
        Software.loadFromFile();
        AudioRules.loadFromFile();
        //VideoRules.loadFromFile();
        AudioCaptureManager.init(getApplicationContext());
        //VideoCaptureManager.init(getApplicationContext());
        GPS.init(getApplicationContext());
        GPS gps = new GPS();
        gps.update(null);
        GPS.init(getApplicationContext());
    }

    public static double getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return level / (float)scale;
    }

    public void testCode(View v) {
    }

    public static Context getContext(){
        return context;
    }
}