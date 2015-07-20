package com.thecacophonytrust.cacophonometer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.recording.RecordingArray;
import com.thecacophonytrust.cacophonometer.util.LoadData;
import com.thecacophonytrust.cacophonometer.recording.PlayRecording;
import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.recording.Recording;
import com.thecacophonytrust.cacophonometer.recording.RecordingAlarm;
import com.thecacophonytrust.cacophonometer.recording.RecordingUploadManager;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;
import com.thecacophonytrust.cacophonometer.recording.RecordingDataObject;

import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = "MainActivity.java";
    private static MainActivity mainActivity; // Holds the most recent Main activity

    @Override
    public void onBackPressed() {
        //TODO make exit if pressed twice in a row
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "MainActivity created.");
        mainActivity = this;    //Saving this activity statically so can be accessed outside by other classes
        RulesArray.clear();
        RecordingArray.clear();
        LoadData.loadSettings();
        LoadData.loadRules();   //Loads the rules found in the set rules folder
        LoadData.loadRecordings();
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
        Log.d(LOG_TAG, "Resuming main activity");
        RecordingAlarm.update(getApplicationContext()); //Updates the alarm for the next recording
        RecordingUploadManager.update();
        updateText();   //Updates the text displayed on the screen for MainActivity
        displayNextRecordingStatus();
        super.onResume();
    }

    /**
     * Displays a Toast of the recording status (time until recording, device is recording, no recording found).
     */
    private void displayNextRecordingStatus(){
        String text;
        if (Recording.isRecording())
            text = "Device is recording";
        else if (RulesArray.getNextRule() == null)
            text = "No rules found for recording";
        else {
            long timeToNextRecording = RecordingAlarm.timeUntilRecording();
            int hours = (int) (timeToNextRecording / 3600);
            int minutes = (int) (timeToNextRecording % 3600) / 60;
            int seconds = (int) timeToNextRecording % 60;
            text = String.format("Next recording starts in %02d:%02d:%02d.", hours, minutes, seconds);
        }
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the text that is displayed.
     */
    private void updateText() {
        Rule nextRule = RulesArray.getNextRule();
        RecordingDataObject lastRDO = Recording.getLastRecordingDataObject();
        TextView infoTextView = (TextView) findViewById(R.id.info_text);
        TextView lastRecordingTextView = (TextView) findViewById(R.id.last_recording_text);

        if (nextRule == null){
            infoTextView.setText("No rule found for next recording");
        } else {
            String time = nextRule.getTimeAsString();
            String name = nextRule.getName();
            infoTextView.setText(String.format("Next recording at %s for rule '%s'", time, name));
        }

        if (lastRDO == null){
            lastRecordingTextView.setText("Last recording not found");
        } else {
            lastRecordingTextView.setText("Last recording from rule '"+lastRDO.getRuleName()+"'");
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
        RecordingDataObject rdo = Recording.getLastRecordingDataObject();
        if (rdo == null) {
            Toast.makeText(getApplicationContext(), "No recording found from this session", Toast.LENGTH_SHORT).show();
        } else {
            PlayRecording.play(rdo, getApplicationContext());
        }
    }

    /**
     * Returns the main method, enables other classes to access the main activity context.
     * @return The MainActivity that was last created
     */
    public static MainActivity getCurrent() {
        return mainActivity;
    }

    public String getTelephonyManagerId(){
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * Gets data from the BUILD class and stores in a JSON object
     * @return JSON object with phone build data.
     */
    public static JSONObject getPhoneBuild(){
        JSONObject phoneBuildJSON = new JSONObject();
        try {
            phoneBuildJSON.put("BOARD", Build.BOARD);
            phoneBuildJSON.put("BOOTLOADER", Build.BOOTLOADER);
            phoneBuildJSON.put("BRAND", Build.BRAND);
            phoneBuildJSON.put("DEVICE", Build.DEVICE);
            phoneBuildJSON.put("DISPLAY", Build.DISPLAY);
            phoneBuildJSON.put("FINGERPRINT", Build.FINGERPRINT);
            phoneBuildJSON.put("HARDWARE", Build.HARDWARE);
            phoneBuildJSON.put("HOST", Build.HOST);
            phoneBuildJSON.put("ID", Build.ID);
            phoneBuildJSON.put("MANUFACTURER", Build.MANUFACTURER);
            phoneBuildJSON.put("MODEL", Build.MODEL);
            phoneBuildJSON.put("PRODUCT", Build.PRODUCT);
            phoneBuildJSON.put("SERIAL", Build.SERIAL);
            phoneBuildJSON.put("TYPE", Build.TYPE);
        } catch (Exception e){
            Log.e(LOG_TAG, "Error with getting phone build data");
            Log.e(LOG_TAG, e.getMessage());
        }
        return phoneBuildJSON;
    }

    /**
     * Returns data from the Build.VERSION in a JSON object
     * @return JSON object with phone build version data.
     */
    public static JSONObject getBuildVersion(){
        JSONObject phoneBuildVersionJSON = new JSONObject();
        try{
            phoneBuildVersionJSON.put("CODENAME", Build.VERSION.CODENAME);
            phoneBuildVersionJSON.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
            phoneBuildVersionJSON.put("SDK_INT", Build.VERSION.SDK_INT);
            phoneBuildVersionJSON.put("RELEASE", Build.VERSION.RELEASE);

        } catch (Exception e){
            Log.e(LOG_TAG, "Error with getting phone build version data");
            Log.e(LOG_TAG, e.getMessage());
        }
        return phoneBuildVersionJSON;
    }
}