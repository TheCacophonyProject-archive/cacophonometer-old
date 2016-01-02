package com.thecacophonytrust.cacophonometer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.recording.RecordingManager;
import com.thecacophonytrust.cacophonometer.resources.Hardware;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.resources.Rule;
import com.thecacophonytrust.cacophonometer.resources.Software;
import com.thecacophonytrust.cacophonometer.util.GPS;
import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.util.JsonFile;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity.java";
    private static Context context = null;

    @Override
    public void onBackPressed() {
        //TODO make exit if pressed twice in a row
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(LOG_TAG, "MainActivity created.");
        init();

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
        String text;
        if (RecordingManager.isRecording())
            text = "Device is recording now.";
        else if (Rule.getNextRuleKey() == 0)
            text = "No rules found for recording";
        else {
            //TODO this could be done better... but works for now.
            Calendar timeOfRecording = RecordingManager.getStartTime();

            if (timeOfRecording != null) {
                long timeToNextRecording = (timeOfRecording.getTimeInMillis() - System.currentTimeMillis())/1000;
                int hours = (int) (timeToNextRecording / 3600);
                int minutes = (int) (timeToNextRecording % 3600) / 60;
                int seconds = (int) timeToNextRecording % 60;
                text = String.format("Next recording starts in %02d:%02d:%02d.", hours, minutes, seconds);
            } else {
                text = "No rules found for recording.";
            }
        }
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the text that is displayed.
     */
    private void updateText() {
        int nextRuleKey = Rule.getNextRuleKey();
        TextView infoTextView = (TextView) findViewById(R.id.info_text);
        TextView lastRecordingTextView = (TextView) findViewById(R.id.last_recording_text);

        if (nextRuleKey == 0){
            infoTextView.setText("No rule found for next recording");
        } else {
            //TODO
            int hour = Rule.getRuleDO(nextRuleKey).nextRecordingTime().get(Calendar.HOUR_OF_DAY);
            int minute = Rule.getRuleDO(nextRuleKey).nextRecordingTime().get(Calendar.MINUTE);
            String name = Rule.getRuleDO(nextRuleKey).name;
            infoTextView.setText(String.format("Next recording at %02d:%02d for rule '%s'", hour, minute, name));
        }
        //TODO
/*
        if (lastRDO == null){
            lastRecordingTextView.setText("Last recording not found");
        } else {
            lastRecordingTextView.setText("Last recording from rule '"+lastRDO.getRuleName()+"'");
        }
        */
    }

    /**
     * Opens the Settings activity
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the Rules activity
     */
    public void openRules(View v) {
        Intent intent = new Intent(this, RulesMenuActivity.class);
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
        context = getApplicationContext();
        Settings.setFromJSON(JsonFile.getJSON(Settings.getSettingsFile().getAbsolutePath()));
        Location.loadFromFile();
        Hardware.loadFromFile();
        Software.loadFromFile();
        Rule.loadFromFile();
        RecordingManager.init(getApplicationContext());
        GPS.init(getApplicationContext());
        GPS gps = new GPS();
        gps.update(null);
        GPS.init(getApplicationContext());
    }

    public void testCode(View v) {
        Logger.i(LOG_TAG, "Starting test code");
    }

    public static Context getContext(){
        return context;
    }
}