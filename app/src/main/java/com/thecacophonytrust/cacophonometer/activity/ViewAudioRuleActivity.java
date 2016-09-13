package com.thecacophonytrust.cacophonometer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.audioRecording.AudioCaptureManager;
import com.thecacophonytrust.cacophonometer.enums.RuleRepeatType;
import com.thecacophonytrust.cacophonometer.resources.AudioRules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewAudioRuleActivity extends AppCompatActivity {

	private static final String LOG_TAG = "ViewAudioRuleActivity.java";

	private AudioRules.DataObject rule = null;
	private int ruleKey = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		ruleKey = intent.getIntExtra("RULE_KEY", 0);
		rule = AudioRules.getRuleDO(ruleKey);
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
		TextView lastRecordingTime = (TextView) findViewById(R.id.view_rule_last_recording_time);

		//ruleNameTextView.setText("Name: " + rule.toString());
		ruleNameTextView.setText("Name: " + rule.getName());
		//recordingTimeTextView.setText("Recording time: " + "Insert time here...");
        int ruleMinute = rule.minute;
        String ruleMinuteStr;
        if (ruleMinute < 10){
            ruleMinuteStr = "0" + ruleMinute;
        }else {
            ruleMinuteStr = String.valueOf(ruleMinute);
        }
	//	recordingTimeTextView.setText("Recording time: " + rule.hour + ":" + ruleMinuteStr);
		recordingLengthTextView.setText("Recording duration: " + rule.getDuration());
		if (rule.lastRecordingTime == null) {   // Checks if there is a time for the last recording
			lastRecordingTime.setText("Last recording time: No recordings for this rule have been made since the phone was last turned on.");
		} else {
			DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
			String timeAsString = dateFormatDateTime.format(rule.lastRecordingTime);
			lastRecordingTime.setText("Last recording time: "+ timeAsString);
		}

		if (rule.repeatType == RuleRepeatType.DAILY){
			recordingTimeTextView.setText("This recording will repeat every day at " + rule.hour + ":" + ruleMinuteStr);

		}else if(rule.repeatType == RuleRepeatType.HOURLY){

			recordingTimeTextView.setText("This rule repeats hourly on the " + ruleMinuteStr + " minute of each hour");
		}
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
		AudioRules.delete(ruleKey);
		AudioCaptureManager.update();
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
