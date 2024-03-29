package com.thecacophonytrust.cacophonometer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoCaptureManager;

/**
 * This class was used to view video rules. But is no longer used/maintained.
 * We use to have an Arduino that had a PIR connected to it (motion sensor) and when the PIR detect
 * motion the Arduino would connect to the Android phone telling it to start recording a video.
 * This class is no longer used as we are using a Raspberry Pi for video recording and other
 * experimental ideas.
 */
public class ViewVideoRuleActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ViewVideoRuleActivity.java";

    private VideoRules.DataObject rule = null;
    private int ruleKey = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ruleKey = intent.getIntExtra("RULE_KEY", 0);
        rule = VideoRules.getRuleDO(ruleKey);
        setContentView(R.layout.activity_view_rule);
        updateText();
        updateRecordingsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_rule, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the displayed text about the rule.
     */
    private void updateText(){
        TextView ruleNameTextView = (TextView) findViewById(R.id.view_rule_rule_name);
        TextView recordingTimeTextView = (TextView) findViewById(R.id.view_rule_recording_time);
        TextView recordingLengthTextView = (TextView) findViewById(R.id.view_rule_recording_length);
        ruleNameTextView.setText("Name: " + rule.toString());
        recordingTimeTextView.setText("Recording time: " + "Insert time here...");
        recordingLengthTextView.setText("Recording duration: " + rule.getDuration());
    }

    /**
     * Updates the list of recordings that are displayed, only displays recording for this rule.
     */
    private void updateRecordingsList(){
        //TODO
    }

    /**
     * Deletes the rule.
     * @param view
     */
    public void delete(View view){
        VideoRules.delete(ruleKey);
        VideoCaptureManager.update();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

