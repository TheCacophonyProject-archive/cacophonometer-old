package com.thecacophonytrust.cacophonometer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.misc.Rule;
import com.thecacophonytrust.cacophonometer.misc.RulesArray;

public class NewRuleActivity extends ActionBarActivity {

	final static private String LOG_TAG = "NewRuleActivity.java";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_rule);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new_rule, menu);
		return true;
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
		EditText length = (EditText) findViewById(R.id.length_input);
		EditText name = (EditText) findViewById(R.id.edit_rule_name);
		TimePicker startTime = (TimePicker) findViewById(R.id.timePicker1);

		if (name.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Enter name for the rule", Toast.LENGTH_SHORT).show();
		} else if (length.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Enter duration of recording", Toast.LENGTH_SHORT).show();
		} else {
			Rule rule = new Rule();
			rule.setName(name.getText().toString());
			rule.setStartTimeHour(startTime.getCurrentHour());
			rule.setStartTimeMinute(startTime.getCurrentMinute());
			rule.setDuration(Integer.parseInt(length.getText().toString()));
			if (rule.isValid()){
				RulesArray.addRule(rule);
				rule.save();
				finish();
			}
		}
	}
}
