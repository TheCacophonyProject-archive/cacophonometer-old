package com.thecacophonytrust.cacophonometer.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.resources.Rule;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONObject;

import java.util.Calendar;

public class NewRuleActivity extends AppCompatActivity {

	final static private String LOG_TAG = "NewRuleActivity.java";

	private int hour = -1;
	private int minute = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_rule);
		Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		updateSetStartTimeText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new_rule, menu);
		return true;
	}

	private void updateSetStartTimeText(){
		Button startTimeEdit = (Button) findViewById(R.id.new_rule_set_start_time);
		String s = String.format("%02d:%02d", hour, minute);
		startTimeEdit.setText(s);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Cancels the activity and returns to the previous activity.
	 * @param view
	 */
	public void cancelRule(View view){
		finish();
	}

	/**
	 * Gets values from inputs and saves as a rule if required fields are filled .
	 * @param view
	 */
	public void saveRule(View view){
		EditText duration = (EditText) findViewById(R.id.length_input);
		EditText name = (EditText) findViewById(R.id.edit_rule_name);

		if (name.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Enter name for the rule", Toast.LENGTH_SHORT).show();
		} else if (duration.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Enter duration of recording", Toast.LENGTH_SHORT).show();
		} else {
			JSONObject rule = new JSONObject();
			try {
				rule.put("name", name.getText().toString());
				rule.put("startTimestamp", hour+":"+minute);
				rule.put("duration", Integer.parseInt(duration.getText().toString()));
				Rule.addAndSave(rule);
				super.onBackPressed();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Error with saving rule", Toast.LENGTH_SHORT).show();
				Logger.e(LOG_TAG, "Error with saving rule.");
				Logger.exception(LOG_TAG, e);
			}
		}
	}

	/**
	 * Opens up a Time picker to enter in time of start of recording.
	 * @param view
	 */
	public void setTime(View view){
		TimePickerDialog timePicker = new TimePickerDialog(this, timePickerListener, hour, minute, true);
		timePicker.show();
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener =
			new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker view, int selectedHour,
									  int selectedMinute) {
					hour = selectedHour;
					minute = selectedMinute;
					updateSetStartTimeText();
				}
			};
}
