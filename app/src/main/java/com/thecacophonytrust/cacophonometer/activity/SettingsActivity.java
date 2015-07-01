package com.thecacophonytrust.cacophonometer.activity;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.GPS;

public class SettingsActivity extends ActionBarActivity {

	private static final String LOG_TAG = "SettingsActivity.java";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
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
	 * Gets values from text fields and saves values if valid
	 * @param view
	 */
	public void saveSettings(View view){
		//Get inputted values
		EditText locationEditText = (EditText) findViewById(R.id.edit_location);
		EditText serverIPEditText = (EditText) findViewById(R.id.edit_server);
		
		//Saving values in Settings class
		String location = locationEditText.getText().toString();
		String serverString = serverIPEditText.getText().toString();
		
		if (!location.equals(""))
			Settings.setLocation(location);
		if (!serverString.equals("")){
			try {
				//Checking if valid url
				@SuppressWarnings("unused")
				URL url = new URL(serverString);
				Settings.setServerUrl(serverString);
			} catch (MalformedURLException e){
				Log.d(LOG_TAG, "Server URL is not valid '"+serverString+"'");
			}
		}
		Settings.saveToFile();
	}

	@Override
	public void onResume(){
		updateTextFields();
		super.onResume();
	}

	/**
	 * Updates text fields
	 */
	private void updateTextFields(){
		EditText locationEditText = (EditText) findViewById(R.id.edit_location);
		EditText serverIPEditText = (EditText) findViewById(R.id.edit_server);
		locationEditText.setText(Settings.getLocation());
		serverIPEditText.setText(Settings.getServerUrl());
	}

	/**
	 * Sends a request for a GPS location update.
	 * @param view
	 */
	public void getGPSLocation(View view){
		Log.v(LOG_TAG, "Get GPS Location button pressed in settings");
		GPS gps = new GPS();
		gps.update();
	}
}
