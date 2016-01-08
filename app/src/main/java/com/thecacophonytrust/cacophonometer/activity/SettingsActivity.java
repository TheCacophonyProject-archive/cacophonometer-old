package com.thecacophonytrust.cacophonometer.activity;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.Settings;
import com.thecacophonytrust.cacophonometer.resources.Location;
import com.thecacophonytrust.cacophonometer.util.GPS;
import com.thecacophonytrust.cacophonometer.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

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

		//if (!userLocationInput.equals(""))
			//Settings.getLocation().setUserLocationInput(userLocationInput);

		if (!serverString.equals("")){
			try {
				//Checking if valid url
				@SuppressWarnings("unused")
				URL url = new URL(serverString);
				Settings.setServerUrl(serverString);
				Settings.saveToFileAsJSON();
				Toast.makeText(getApplicationContext(), "Settings updated.", Toast.LENGTH_SHORT).show();
			} catch (MalformedURLException e){
				Logger.d(LOG_TAG, "Server URL is not valid '" + serverString + "'");
				Toast.makeText(getApplicationContext(), "Malformed URL for server.", Toast.LENGTH_SHORT).show();
			}
		}

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

        //TODO
		//locationEditText.setText(Settings.getLocation().getUserLocationInput());
		serverIPEditText.setText(Settings.getServerUrl());

		updateLocationTextField();
	}


	/**
	 * Updates the text fields of just the location.
	 */
	public void updateLocationTextField(){
        JSONObject location = Location.getMostRecent();
		TextView gpsInfoTextView = (TextView) findViewById(R.id.settings_gps_info);
		String altitude;
		if (location == null) {
			Logger.i(LOG_TAG, "No location info found.");
			return;
		}
		try {
            if (location.has("altitude"))
                altitude = String.format("%.1fm", location.getDouble("altitude"));
            else
                altitude = "Altitude not found.";

            //Make accuracy message
            String accuracy = String.format("%.1fm", location.getDouble("accuracy"));

            String locationInfo = String.format("Latitude: %f\nLongitude: %f\nAltitude: %s\nAccuracy: %s\nTime: %s",
                    location.getDouble("latitude"), location.getDouble("longitude"), altitude,
                    accuracy, location.getString("timestamp"));
            gpsInfoTextView.setText(locationInfo);
        } catch (JSONException e) {
            Logger.e(LOG_TAG, "Error with loading info from location json");
        }
	}

	/**
	 * Sends a request for a GPS location update.
	 * @param view
	 */
	public void getGPSLocation(View view){
		Logger.v(LOG_TAG, "Get GPS Location button pressed in settings");
		Location.getNewLocation();
        GPS gps = new GPS();
		Logger.d(LOG_TAG, this.toString());
		gps.update(this);
	}
}
