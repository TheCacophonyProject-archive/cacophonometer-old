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
import android.widget.TextView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.util.GPS;
import com.thecacophonytrust.cacophonometer.util.Location;

public class SettingsActivity extends ActionBarActivity {

	private static final String LOG_TAG = "SettingsActivity.java";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		updateTextFields();
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
		EditText serverIPEditText = (EditText) findViewById(R.id.edit_server);
		EditText userLocationInputEditText = (EditText) findViewById(R.id.settings_user_location_input);

		
		//Saving values in Settings class
		String serverString = serverIPEditText.getText().toString();
		String userLocationInput = userLocationInputEditText.getText().toString();

		if (!userLocationInput.equals(""))
			Settings.getLocation().setUserLocationInput(userLocationInput);

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
		Settings.saveToFileAsJSON();
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
		EditText locationEditText = (EditText) findViewById(R.id.settings_user_location_input);
		EditText serverIPEditText = (EditText) findViewById(R.id.edit_server);

		locationEditText.setText(Settings.getLocation().getUserLocationInput());
		serverIPEditText.setText(Settings.getServerUrl());

		updateLocationTextField();
	}


	/**
	 * Updates the text fields of just the location.
	 */
	public void updateLocationTextField(){
		Location location = Settings.getLocation();
		TextView gpsInfoTextView = (TextView) findViewById(R.id.settings_gps_info);
		String altitude;
		if (location.hasAltitude())
			altitude = String.format("%.1fm", location.getAltitude());
		else
			altitude = "Altitude not found.";

		//Make accuracy message
		String accuracy = String.format("%.1fm", location.getAccuracy());

		String locationInfo = String.format("Latitude: %f\nLongitude: %f\nAltitude: %s\nAccuracy: %s\nTime: %d",
				location.getLatitude(), location.getLongitude(), altitude,
				accuracy, location.getGPSLocationTime());
		gpsInfoTextView.setText(locationInfo);
	}

	/**
	 * Sends a request for a GPS location update.
	 * @param view
	 */
	public void getGPSLocation(View view){
		Log.v(LOG_TAG, "Get GPS Location button pressed in settings");
		GPS gps = new GPS();
		Log.d(LOG_TAG, this.toString());
		gps.update(this);
	}
}
