package com.thecacophonytrust.cacophonometer.activity;

import java.util.ArrayList;
import java.util.List;

import com.thecacophonytrust.cacophonometer.recording.RecordingDataObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thecacophonytrust.cacophonometer.recording.PlayRecording;
import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.recording.RecordingArray;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;

public class ViewRuleActivity extends ActionBarActivity {

	private static final String LOG_TAG = "ViewRuleActivity.java";
	private List<RecordingDataObject> listRDO;
	
	private Rule rule = null;
	private Intent intent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		rule = RulesArray.getByName(intent.getAction());
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
		ruleNameTextView.setText("Name: " + rule.getName());
		recordingTimeTextView.setText("Recording time: " + rule.getTimeAsString());
		recordingLengthTextView.setText("Recording duration: " + rule.getDuration());
	}

	/**
	 * Updates the list of recordings that are displayed, only displays recording for this rule.
	 */
	private void updateRecordingsList(){
		ListView lv = (ListView) findViewById(R.id.view_rule_recording_list);
		List<RecordingDataObject> listRDOToUpload = RecordingArray.getRecordingsToUploadByRule(rule.getName());
		List<RecordingDataObject> listRDOUploaded = RecordingArray.getUploadedRecordingsByRule(rule.getName());
		Log.d(LOG_TAG, "to upload " + listRDOToUpload);
		Log.d(LOG_TAG, "to upload " + listRDOUploaded);
		
		listRDO = new ArrayList<>();
		listRDO.addAll(listRDOToUpload);
		listRDO.addAll(listRDOUploaded);
		
		List<String> listDisplayArray = new ArrayList<>();
		for (RecordingDataObject rdo : listRDO){
			listDisplayArray.add(rdo.toString());
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDisplayArray);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(LOG_TAG, "Getting rule at position " + position);
				PlayRecording.play(listRDO.get(position), getApplicationContext());
			}
		});
	}

	/**
	 * Deletes the rule.
	 * @param view
	 */
	public void delete(View view){
		rule.delete();
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		PlayRecording.stop();
		super.onBackPressed();
	}
}
